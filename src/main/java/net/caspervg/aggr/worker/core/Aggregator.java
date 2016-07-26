package net.caspervg.aggr.worker.core;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AbstractAggregation;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.util.AggrContext;

@FunctionalInterface
public interface Aggregator<A extends AbstractAggregation, M> {
    /**
     * Aggregates certain measurements of a dataset
     *
     * @param dataset Dataset of the measurements
     * @param measurements Measurements to aggregate, filter, ...
     * @param context Context of the operation
     * @return Result of the aggregation
     */
    Iterable<AggregationResult<A, M>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context);
}
