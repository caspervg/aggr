package net.caspervg.aggr.worker.average;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AverageAggregation;

import java.io.Serializable;
import java.util.ArrayList;

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
