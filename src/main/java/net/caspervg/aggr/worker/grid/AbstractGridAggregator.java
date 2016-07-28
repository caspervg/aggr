package net.caspervg.aggr.worker.grid;

import net.caspervg.aggr.worker.core.bean.Measurement;

public abstract class AbstractGridAggregator implements GridAggregator {
    public static final String GRID_SIZE_PARAM = "grid_size";

    protected static final String DEFAULT_GRID_SIZE = "0.0005";

    protected Measurement newInstance(Class<? extends Measurement> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
