package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractDiffAggregator extends AbstractAggregator<DiffAggregation, Measurement> implements DiffAggregator, Serializable {

    public static final String SUBTRAHEND_PARAM_KEY = "subtrahends";

    protected Iterable<Iterable<Measurement>> others;

    public AbstractDiffAggregator() {
        this(new ArrayList<>());
    }

    public AbstractDiffAggregator(Iterable<Iterable<Measurement>> others) {
        this.others = others;
    }

}
