package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;

public abstract class AbstractDiffAggregator extends AbstractAggregator<DiffAggregation, Measurement> implements DiffAggregator {

    public static final String SUBTRAHEND_PARAM_KEY = "subtrahend";

    protected Iterable<Measurement> subtrahends;

    public AbstractDiffAggregator(Iterable<Measurement> subtrahends) {
        this.subtrahends = subtrahends;
    }
}
