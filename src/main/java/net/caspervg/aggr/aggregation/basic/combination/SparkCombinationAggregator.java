package net.caspervg.aggr.aggregation.basic.combination;

import net.caspervg.aggr.aggregation.basic.BasicAggregator;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.aggregation.basic.BasicAggregation;
import net.caspervg.aggr.core.util.AggrContext;

/**
 * {@inheritDoc}
 *
 * Combines measurements in the same dataset if they are compatible (e.g. identical vector, ...)
 *
 * @implNote Not yet implemented, delegates to {@link PlainCombinationAggregator} instead.
 */
@Deprecated
public class SparkCombinationAggregator implements BasicAggregator {
    @Override
    public Iterable<AggregationResult<BasicAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        return new PlainCombinationAggregator().aggregate(dataset, measurements, context);
    }
}
