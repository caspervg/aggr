package net.caspervg.aggr.aggregation.time;

import net.caspervg.aggr.worker.command.AggrCommand;
import net.caspervg.aggr.worker.command.TimeAggrCommand;
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

public class TimeAggregationExecution extends AbstractAggregationExecution {

    private AggrCommand ac;
    private TimeAggrCommand tac;

    public TimeAggregationExecution(AggrCommand ac, TimeAggrCommand tac) {
        this.ac = ac;
        this.tac = tac;
    }

    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractTimeAggregator.DETAIL_PARAM, String.valueOf(tac.getMaxDetail()));

        TimeAggregator aggregator;
        if (ac.isSpark()) {
            aggregator = new SparkTimeAggregator();
        } else {
            aggregator = new PlainTimeAggregator();
        }
        AggrContext ctx = createContext(params, ac);

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
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
}
