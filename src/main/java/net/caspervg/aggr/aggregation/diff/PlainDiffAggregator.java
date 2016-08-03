package net.caspervg.aggr.aggregation.diff;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.core.util.AggrContext;

/**
 * {@inheritDoc}
 *
 * @implNote Not yet implemented
 */
public class PlainDiffAggregator extends AbstractDiffAggregator {

    public PlainDiffAggregator(Iterable<Measurement> subtrahends) {
        super(subtrahends);
    }

    @Deprecated
    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> minuends, AggrContext context) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
