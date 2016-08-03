package net.caspervg.aggr.aggregation.diff;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractDiffAggregator extends AbstractAggregator<DiffAggregation, Measurement> implements DiffAggregator, Serializable {

    public static final String OTHER_PARAM_KEY = "other";
    public static final String KEY_PARAM_KEY    = "key";
    protected static final String DEFAULT_KEY = "weight";

    protected Iterable<Measurement> subtrahends;

    public AbstractDiffAggregator() {
        this(new ArrayList<>());
    }

    public AbstractDiffAggregator(Iterable<Measurement> subtrahends) {
        this.subtrahends = subtrahends;
    }

}
