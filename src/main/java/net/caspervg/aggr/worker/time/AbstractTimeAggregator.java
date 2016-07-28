package net.caspervg.aggr.worker.time;

import net.caspervg.aggr.worker.core.bean.Measurement;

public abstract class AbstractTimeAggregator implements TimeAggregator {
    public static final String DETAIL_PARAM = "detail";

    protected static final String DEFAULT_NUM_DETAIL = "8";

    protected Measurement newInstance(Class<? extends Measurement> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
