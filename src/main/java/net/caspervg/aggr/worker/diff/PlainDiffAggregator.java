package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;

public class PlainDiffAggregator extends AbstractDiffAggregator {

    public PlainDiffAggregator(Iterable<Measurement> subtrahends) {
        super(subtrahends);
    }

    @Deprecated
    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> minuends, AggrContext context) {
        return null;
    }
}
