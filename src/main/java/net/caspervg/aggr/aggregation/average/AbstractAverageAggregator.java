package net.caspervg.aggr.aggregation.average;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Computes the average of a data point for a set of measurements. The measurements to aggregates should be set in
 * {@link #AbstractAverageAggregator(Iterable)}. The expected amount of measurements per vector should be set using {@link #AMOUNT_PARAM_KEY},
 * while the key to extract the data point can be set using {@link #KEY_PARAM_KEY}.
 */
public abstract class AbstractAverageAggregator extends AbstractAggregator<AverageAggregation, Measurement> implements AverageAggregator, Serializable {

    public static final String OTHERS_PARAM_KEY = "others";
    public static final String AMOUNT_PARAM_KEY = "amount";
    public static final String KEY_PARAM_KEY    = "key";
    protected static final String DEFAULT_KEY = "weight";

    protected Iterable<Iterable<Measurement>> others;

    public AbstractAverageAggregator() {
        this(new ArrayList<>());
    }

    public AbstractAverageAggregator(Iterable<Iterable<Measurement>> others) {
        this.others = others;
    }

    protected String defaultAmount() {
        return String.valueOf(Iterables.size(others));
    }
}
