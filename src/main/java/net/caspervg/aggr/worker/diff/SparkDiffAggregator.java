package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;

@Deprecated
public class SparkDiffAggregator extends AbstractDiffAggregator {

    public SparkDiffAggregator(Iterable<Measurement> subtrahends) {
        super(subtrahends);
    }

    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        return new PlainDiffAggregator(subtrahends).aggregate(dataset, measurements, context);
    }
}
