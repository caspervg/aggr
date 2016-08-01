package net.caspervg.aggr.worker.core;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AbstractAggregation;

public abstract class AbstractAggregator<A extends AbstractAggregation, M> implements Aggregator<A, M> {
    protected Measurement newInstance(Class<? extends Measurement> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
