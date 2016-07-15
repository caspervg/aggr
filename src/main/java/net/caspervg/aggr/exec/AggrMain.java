package net.caspervg.aggr.exec;

import com.beust.jcommander.JCommander;
import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.read.AbstractAggrReader;
import net.caspervg.aggr.core.read.AggrReader;
import net.caspervg.aggr.core.read.CsvAggrReader;
import net.caspervg.aggr.core.read.JenaAggrReader;
import net.caspervg.aggr.core.util.AggrCommand;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.untyped.UntypedSPARQLRepository;
import net.caspervg.aggr.core.write.AggrWriter;
import net.caspervg.aggr.core.write.CsvAggrWriter;
import net.caspervg.aggr.core.write.Rdf4jAggrWriter;
import net.caspervg.aggr.grid.AbstractGridAggregator;
import net.caspervg.aggr.grid.GridAggregator;
import net.caspervg.aggr.grid.PlainGridAggregator;
import net.caspervg.aggr.grid.SparkGridAggregator;
import net.caspervg.aggr.grid.util.GridAggrCommand;
import net.caspervg.aggr.kmeans.AbstractKMeansAggregator;
import net.caspervg.aggr.kmeans.KMeansAggregator;
import net.caspervg.aggr.kmeans.SparkKMeansAggregator;
import net.caspervg.aggr.kmeans.util.KMeansAggrCommand;
import net.caspervg.aggr.time.AbstractTimeAggregator;
import net.caspervg.aggr.time.PlainTimeAggregator;
import net.caspervg.aggr.time.SparkTimeAggregator;
import net.caspervg.aggr.time.TimeAggregator;
import net.caspervg.aggr.time.util.TimeAggrCommand;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class AggrMain {
    public static void main(String[] args) throws IOException, URISyntaxException {
        AggrCommand ac = new AggrCommand();
        JCommander jc = new JCommander(ac);

        GridAggrCommand gac = new GridAggrCommand();
        jc.addCommand("grid", gac);
        TimeAggrCommand tac = new TimeAggrCommand();
        jc.addCommand("time", tac);
        KMeansAggrCommand kac = new KMeansAggrCommand();
        jc.addCommand("kmeans", kac);

        jc.parse(args);

        if (jc.getParsedCommand() == null) {
            jc.usage();
            return;
        }

        switch(jc.getParsedCommand()) {
            case "grid":
                handleGridCommand(ac, gac);
                break;
            case "time":
                handleTimeCommand(ac, tac);
                break;
            case "kmeans":
                handleKMeansCommand(ac, kac);
                break;
            default:
                jc.usage();
        }
    }

    private static void handleKMeansCommand(AggrCommand ac, KMeansAggrCommand kac) throws IOException, URISyntaxException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractKMeansAggregator.CENTROIDS_PARAM, String.valueOf(kac.getNumCentroids()));
        params.put(AbstractKMeansAggregator.ITERATIONS_PARAM, String.valueOf(kac.getIterations()));
        params.put(AbstractKMeansAggregator.METRIC_PARAM, kac.getDistanceMetricChoice().name());

        AggrContext ctx;
        KMeansAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = System.getenv("HDFS_URL");
            JavaSparkContext sparkCtx = getSparkContext(ac);
            if (hdfsUrl != null) {
                FileSystem hdfs = FileSystem.get(new URI(hdfsUrl), sparkCtx.hadoopConfiguration());
                ctx = new AggrContext(params, sparkCtx, hdfs);
            } else {
                ctx = new AggrContext(params, sparkCtx);
            }
            aggregator = new SparkKMeansAggregator();
        } else {
            ctx = new AggrContext(params);
            throw new UnsupportedOperationException("Plain KMeans aggregator has not yet been implemented");
            // aggregator = new PlainKmeansAggregator();
        }

        Dataset dataset = new Dataset("kmeans_dataset");
        Iterable<Measurement> meas = getReader(ac).read(ctx);
        Iterable<AggregationResult<KMeansAggregation, Centroid>> results = aggregator.aggregate(dataset, meas, ctx);
        AggrWriter writer = getWriter(ac, ctx);

        for (AggregationResult<KMeansAggregation, Centroid> res : results) {
            KMeansAggregation gag = res.getAggregation();
            Iterable<Centroid> centroids = res.getResults();

            writer.writeAggregation(gag, ctx);
            writer.writeCentroids(centroids, ctx);
        }
        writer.writeDataset(dataset, ctx);
    }

    private static void handleTimeCommand(AggrCommand ac, TimeAggrCommand tac) throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractTimeAggregator.DETAIL_PARAM, String.valueOf(tac.getMaxDetail()));

        AggrContext ctx;
        TimeAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = System.getenv("HDFS_URL");
            JavaSparkContext sparkCtx = getSparkContext(ac);
            if (hdfsUrl != null) {
                FileSystem hdfs = FileSystem.get(new URI(hdfsUrl), sparkCtx.hadoopConfiguration());
                ctx = new AggrContext(params, sparkCtx, hdfs);
            } else {
                ctx = new AggrContext(params, sparkCtx);
            }
            aggregator = new SparkTimeAggregator();
        } else {
            ctx = new AggrContext(params);
            aggregator = new PlainTimeAggregator();
        }

        Dataset dataset = new Dataset("time_dataset");
        Iterable<Measurement> meas = getReader(ac).read(ctx);
        Iterable<AggregationResult<TimeAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);
        AggrWriter writer = getWriter(ac, ctx);

        for (AggregationResult<TimeAggregation, Measurement> res : results) {
            TimeAggregation tag = res.getAggregation();
            Iterable<Measurement> resMeas = res.getResults();

            writer.writeAggregation(tag, ctx);
            writer.writeMeasurements(resMeas, ctx);
        }

        writer.writeDataset(dataset, ctx);
    }

    private static void handleGridCommand(AggrCommand ac, GridAggrCommand gac) throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractGridAggregator.GRID_SIZE_PARAM, String.valueOf(gac.getGridSize()));

        AggrContext ctx;
        GridAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = System.getenv("HDFS_URL");
            JavaSparkContext sparkCtx = getSparkContext(ac);
            if (hdfsUrl != null) {
                FileSystem hdfs = FileSystem.get(new URI(hdfsUrl), sparkCtx.hadoopConfiguration());
                ctx = new AggrContext(params, sparkCtx, hdfs);
            } else {
                ctx = new AggrContext(params, sparkCtx);
            }
            aggregator = new SparkGridAggregator();
        } else {
            ctx = new AggrContext(params);
            aggregator = new PlainGridAggregator();
        }

        Dataset dataset = new Dataset("grid_dataset");
        Iterable<Measurement> meas = getReader(ac).read(ctx);
        Iterable<AggregationResult<GridAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);
        AggrWriter writer = getWriter(ac, ctx);

        for (AggregationResult<GridAggregation, Measurement> res : results) {
            GridAggregation gag = res.getAggregation();
            Iterable<Measurement> resMeas = res.getResults();

            writer.writeAggregation(gag, ctx);
            writer.writeMeasurements(resMeas, ctx);
        }

        writer.writeDataset(dataset, ctx);
    }

    private static JavaSparkContext getSparkContext(AggrCommand ac) {
        SparkConf conf = new SparkConf().setAppName("KMeansAggr").setMaster(ac.getSparkMaster());
        return new JavaSparkContext(conf);
    }

    private static AggrReader getReader(AggrCommand ac) {
        if (ac.getInput().toLowerCase().contains("sparql")) {
            return new JenaAggrReader();
        } else {
            return new CsvAggrReader();
        }
    }

    private static AggrWriter getWriter(AggrCommand ac, AggrContext ctx) {
        if (ac.getOutput().toLowerCase().contains("sparql")) {
            return new Rdf4jAggrWriter(new UntypedSPARQLRepository(ac.getOutput()));
        } else {
            try {
                String path = ctx.getParameters().getOrDefault(OUTPUT_PARAM_KEY, "output.csv");

                if (ac.isSpark()) {
                    if (path.toLowerCase().contains("hdfs")) {
                        FSDataOutputStream os = ctx.getFileSystem().create(new Path(path), true);
                        return new CsvAggrWriter(new PrintWriter(os));
                    } else {
                        return new CsvAggrWriter(new PrintWriter(new File(path)));
                    }
                } else {
                    return new CsvAggrWriter(new PrintWriter(new File(path)));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
