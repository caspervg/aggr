package net.caspervg.aggr.aggregation.basic;

import com.google.common.collect.ImmutableMap;
import net.caspervg.aggr.worker.command.AggrCommand;
import net.caspervg.aggr.aggregation.basic.combination.PlainCombinationAggregator;
import net.caspervg.aggr.aggregation.basic.combination.SparkCombinationAggregator;
import net.caspervg.aggr.aggregation.AbstractAggregationExecution;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.worker.read.AbstractAggrReader;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.worker.write.AggrResultWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.worker.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class BasicAggregationExecution extends AbstractAggregationExecution {

    private static final Map<String, BasicAggregator> SPARK_AGGREGATORS;
    private static final Map<String, BasicAggregator> PLAIN_AGGREGATORS;

    static {
        SPARK_AGGREGATORS = ImmutableMap.of(
                "combination", new SparkCombinationAggregator()
        );
        PLAIN_AGGREGATORS = ImmutableMap.of(
                "combination", new PlainCombinationAggregator()
        );
    }

    private AggrCommand ac;
    private String type;

    public BasicAggregationExecution(AggrCommand ac, String type) {
        this.ac = ac;
        this.type = type;
    }

    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());

        AggrContext ctx = createContext(params, ac);
        BasicAggregator aggregator;
        if (ac.isSpark()) {
            aggregator = SPARK_AGGREGATORS.get(type);
        } else {
            aggregator = PLAIN_AGGREGATORS.get(type);
        }

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<BasicAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<BasicAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeBasicAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }
}
