package net.caspervg.aggr.kmeans;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.read.AbstractAggrReader;
import net.caspervg.aggr.core.AbstractAggregationExecution;
import net.caspervg.aggr.core.util.AggrCommand;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.write.AggrResultWriter;
import net.caspervg.aggr.kmeans.util.KMeansAggrCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class KMeansAggregationExecution extends AbstractAggregationExecution {

    private AggrCommand ac;
    private KMeansAggrCommand kac;

    public KMeansAggregationExecution(AggrCommand ac, KMeansAggrCommand kac) {
        this.ac = ac;
        this.kac = kac;
    }

    @Override
    public void execute() throws URISyntaxException, IOException {
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
            //aggregator = new SparkKMeansAggregator();
            aggregator = new SparkKMeansClusterAggregator();
        } else {
            ctx = new AggrContext(params);
            aggregator = new PlainKMeansAggregator();
        }

        Dataset dataset = Dataset.Builder.setup().withTitle("kmeans_dataset").build();
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
}
