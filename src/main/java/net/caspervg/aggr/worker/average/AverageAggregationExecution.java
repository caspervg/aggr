package net.caspervg.aggr.worker.average;

import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.core.AverageAggrCommand;
import net.caspervg.aggr.worker.core.AbstractAggregationExecution;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.AverageAggregation;
import net.caspervg.aggr.worker.core.read.AbstractAggrReader;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.write.AggrResultWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static net.caspervg.aggr.worker.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class AverageAggregationExecution extends AbstractAggregationExecution {
    
    private AggrCommand ac;
    private AverageAggrCommand dac;

    public AverageAggregationExecution(AggrCommand ac, AverageAggrCommand dac) {
        this.ac = ac;
        this.dac = dac;
    }
    
    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractAverageAggregator.OTHERS_PARAM_KEY, String.join(",", dac.getOthers()));
        params.put(AbstractAverageAggregator.AMOUNT_PARAM_KEY, String.valueOf(dac.getAmount()));
        params.put(AbstractAverageAggregator.KEY_PARAM_KEY, dac.getKey());

        AverageAggregator aggregator;
        AggrContext ctx = createContext(params, ac);

        Iterable<Iterable<Measurement>> others = dac.getOthers()
                .parallelStream()
                .map(other ->
                    uncheckCall(() -> getReader(other, ac, ctx).read(ctx))
                )
                .collect(Collectors.toList());

        if (ac.isSpark()) {
            aggregator = new SparkAverageAggregator(others);
        } else {
            aggregator = new PlainAverageAggregator(others);
        }

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
        Iterable<AggregationResult<AverageAggregation, Measurement>> results = aggregator.aggregate(dataset, new ArrayList<>(), ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<AverageAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeAverageAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }

    public static <T> T uncheckCall(Callable<T> callable) {
        try { return callable.call(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
