package net.caspervg.aggr.core;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AbstractAggregation;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.util.AggrContext;

@FunctionalInterface
public interface Aggregator<A extends AbstractAggregation, M> {
    Iterable<AggregationResult<A, M>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context);
}
