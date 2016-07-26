package net.caspervg.aggr.worker.core.bean.aggregation;

import java.util.Objects;

/**
 * Combines an AbstractAggregation with its result
 *
 * @param <A> Type of aggregation
 * @param <M> Type of the result
 */
public class AggregationResult<A extends AbstractAggregation, M> {

    /**
     * Constructs a new AggregationResult
     *
     * @param aggregation Aggregation that was executed
     * @param results Results of the aggregation
     */
    public AggregationResult(A aggregation, Iterable<M> results) {
        Objects.requireNonNull(aggregation);
        Objects.requireNonNull(results);

        this.aggregation = aggregation;
        this.results = results;
    }

    private A aggregation;
    private Iterable<M> results;

    /**
     * Return the aggregation
     *
     * @return the aggregation
     */
    public A getAggregation() {
        return aggregation;
    }

    /**
     * Return the results of the aggregation
     *
     * @return the aggregation results
     */
    public Iterable<M> getResults() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregationResult)) return false;

        AggregationResult<?, ?> that = (AggregationResult<?, ?>) o;

        if (aggregation != null ? !aggregation.equals(that.aggregation) : that.aggregation != null) return false;
        return results != null ? results.equals(that.results) : that.results == null;

    }

    @Override
    public int hashCode() {
        int result = aggregation != null ? aggregation.hashCode() : 0;
        result = 31 * result + (results != null ? results.hashCode() : 0);
        return result;
    }
}
