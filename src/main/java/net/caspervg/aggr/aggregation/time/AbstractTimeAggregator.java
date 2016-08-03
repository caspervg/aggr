package net.caspervg.aggr.aggregation.time;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

public abstract class AbstractTimeAggregator extends AbstractAggregator<TimeAggregation, Measurement> implements TimeAggregator {
    public static final String DETAIL_PARAM = "detail";

    protected static final String DEFAULT_NUM_DETAIL = "8";

}
