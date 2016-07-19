package net.caspervg.aggr.exec;

import com.beust.jcommander.JCommander;
import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.*;
import net.caspervg.aggr.core.read.AbstractAggrReader;
import net.caspervg.aggr.core.read.AggrReader;
import net.caspervg.aggr.core.read.CsvAggrReader;
import net.caspervg.aggr.core.read.JenaAggrReader;
import net.caspervg.aggr.core.util.AggrCommand;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.untyped.UntypedSPARQLRepository;
import net.caspervg.aggr.core.write.*;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.JsonRelay;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
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
            String hdfsUrl = ac.getHdfsUrl();
            JavaSparkContext sparkCtx = getSparkContext(ac);

            if (StringUtils.isNotBlank(hdfsUrl)) {
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
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<KMeansAggregation, Centroid>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<KMeansAggregation, Centroid> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeKMeansAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }

    private static void handleTimeCommand(AggrCommand ac, TimeAggrCommand tac) throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractTimeAggregator.DETAIL_PARAM, String.valueOf(tac.getMaxDetail()));

        AggrContext ctx;
        TimeAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = ac.getHdfsUrl();
            JavaSparkContext sparkCtx = getSparkContext(ac);

            if (StringUtils.isNotBlank(hdfsUrl)) {
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
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<TimeAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<TimeAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeTimeAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }

    private static void handleGridCommand(AggrCommand ac, GridAggrCommand gac) throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractGridAggregator.GRID_SIZE_PARAM, String.valueOf(gac.getGridSize()));

        AggrContext ctx;
        GridAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = ac.getHdfsUrl();
            JavaSparkContext sparkCtx = getSparkContext(ac);

            if (StringUtils.isNotBlank(hdfsUrl)) {
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
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<GridAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<GridAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeGridAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }

    /**
     * Builds a Spark context for Java execution
     * @param ac Demands of the user
     * @return Spark Context with some
     */
    private static JavaSparkContext getSparkContext(AggrCommand ac) {
        SparkConf conf = new SparkConf()
                .setAppName("KMeansAggr")
                .setMaster(ac.getSparkMasterUrl())
                .set("spark.eventLog.enabled", "true")
                .set("eventLog.enabled", "true");
        return new JavaSparkContext(conf);
    }

    /**
     * Retrieve a suitable {@link AggrReader} based on the user's demands
     *
     * @param ac Demands of the user
     * @param ctx Context of the execution
     * @return Suitable instance of {@link AggrReader} with a pre-set {@link BufferedReader}
     * @throws IOException if the input cannot be opened or read
     */
    private static AggrReader getReader(AggrCommand ac, AggrContext ctx) throws IOException {
        if (ac.getInput().toLowerCase().contains("sparql")) {
            return new JenaAggrReader();
        } else {
            if (!ac.isHdfs()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ac.getInput())));
                return new CsvAggrReader(reader);
            } else {
                Path path = new Path(ac.getInput());
                BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.getFileSystem().open(path)));
                return new CsvAggrReader(reader);
            }
        }
    }

    /**
     * Retrieve a suitable AggrResultWriter based on the user's demands
     *
     * @param aggrResult Type of the aggregation result
     * @param ac Demands of the user
     * @param ctx Execution context
     * @param <A> Type of the aggregation
     * @param <M> Type of the aggregation result
     * @return Suitable writer, possibly a composite {@link AggrWriter} that selects different channels for different
     * types of output
     */
    private static <A extends AbstractAggregation, M> AggrResultWriter getWriter(
            AggregationResult<A, M> aggrResult,
            AggrCommand ac,
            AggrContext ctx) {
        AggrWriter metaWriter = new Rdf4jAggrWriter(new UntypedSPARQLRepository(ac.getService()), ac.isWriteProvenance());
        AggrWriter dataWriter;

        if (ac.isWriteDataCsv()) {
            try {
                String hdfsUrl = ac.getHdfsUrl();
                String dirPath = ac.getOutput();
                String fileName = aggrResult.getAggregation().getUuid() + ".csv";

                if (ac.isSpark()) {
                    if (StringUtils.isNotBlank(hdfsUrl)) {
                        Path parent = new Path(dirPath);
                        Path child = new Path(parent, fileName);
                        FSDataOutputStream os = ctx.getFileSystem().create(child, false);
                        dataWriter = new CsvAggrWriter(new PrintWriter(os));
                    } else {
                        dataWriter = new CsvAggrWriter(new PrintWriter(new File(dirPath + "/" + fileName)));
                    }
                } else {
                    dataWriter = new CsvAggrWriter(new PrintWriter(new File(dirPath + "/" + fileName)));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }

            return new CompositeAggrWriter(dataWriter, metaWriter, ac.isWriteProvenance());     // Split data to CSV, metadata to triple store
        }

        return new CompositeAggrWriter(metaWriter, metaWriter, ac.isWriteProvenance());         // Write data and metadata to the triple store
    }

    /**
     * Stop the Spark Context if it exists
     * @param context Context of the execution
     */
    private static void stop(AggrContext context) {
        if (context.getSparkContext() != null) {
            context.getSparkContext().stop();
        }
    }
}
