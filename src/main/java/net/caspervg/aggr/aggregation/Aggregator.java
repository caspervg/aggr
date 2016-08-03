package net.caspervg.aggr.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.util.AggrContext;

@FunctionalInterface
public interface Aggregator<A extends AbstractAggregation, M> {
    /**
     * Aggregates certain measurements of a dataset
     *
     * @param dataset Dataset of the measurements
     * @param measurements Measurements to aggregate, filter, ...
     * @param context Context of the operation
     * @return Results of the aggregation. The returned Iterable may be a singleton.
     */
    Iterable<AggregationResult<A, M>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context);
}
