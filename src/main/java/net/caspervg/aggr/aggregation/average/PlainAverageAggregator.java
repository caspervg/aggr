package net.caspervg.aggr.aggregation.average;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.core.util.AggrContext;

/**
 * {@inheritDoc}
 *
 * @implNote Not yet implemented
 */
public class PlainAverageAggregator extends AbstractAverageAggregator {

    public PlainAverageAggregator(Iterable<Iterable<Measurement>> others) {
        super(others);
    }

    @Override
    public Iterable<AggregationResult<AverageAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
