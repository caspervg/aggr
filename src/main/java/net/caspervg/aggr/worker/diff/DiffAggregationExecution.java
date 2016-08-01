package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.core.DiffAggrCommand;
import net.caspervg.aggr.worker.core.AbstractAggregationExecution;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;
import net.caspervg.aggr.worker.core.read.AbstractAggrReader;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.write.AggrResultWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.worker.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class DiffAggregationExecution extends AbstractAggregationExecution {
    
    private AggrCommand ac;
    private DiffAggrCommand dac;

    public DiffAggregationExecution(AggrCommand ac, DiffAggrCommand dac) {
        this.ac = ac;
        this.dac = dac;
    }
    
    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());

        DiffAggregator aggregator;
        AggrContext ctx = createContext(params, ac);
        Iterable<Measurement> subtrahends = getReader(dac.getSubtrahend(), ac, ctx).read(ctx);
        if (ac.isSpark()) {
            aggregator = new SparkDiffAggregator(subtrahends);
        } else {
            aggregator = new PlainDiffAggregator(subtrahends);
        }

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<DiffAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<DiffAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeDiffAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);
    }
}
