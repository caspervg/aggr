package net.caspervg.aggr.worker.kmeans;

import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.core.KMeansAggrCommand;
import net.caspervg.aggr.worker.core.AbstractAggregationExecution;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.worker.core.read.AbstractAggrReader;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.write.AggrResultWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.worker.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

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

        AggrContext ctx = createContext(params, ac);
        KMeansAggregator aggregator;
        if (ac.isSpark()) {
            //aggregator = new SparkKMeansAggregator();
            aggregator = new SparkKMeansClusterAggregator();
        } else {
            aggregator = new PlainKMeansAggregator();
        }

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).build();
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<KMeansAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<KMeansAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeKMeansAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }
}
