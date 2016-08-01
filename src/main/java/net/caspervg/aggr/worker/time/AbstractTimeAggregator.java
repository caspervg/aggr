package net.caspervg.aggr.worker.time;

import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.TimeAggregation;

public abstract class AbstractTimeAggregator extends AbstractAggregator<TimeAggregation, Measurement> implements TimeAggregator {
    public static final String DETAIL_PARAM = "detail";

    protected static final String DEFAULT_NUM_DETAIL = "8";

}
