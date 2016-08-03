package net.caspervg.aggr.aggregation;

import net.caspervg.aggr.core.bean.Measurement;

public abstract class AbstractAggregator<A extends AbstractAggregation, M> implements Aggregator<A, M> {
    protected Measurement newInstance(Class<? extends Measurement> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
