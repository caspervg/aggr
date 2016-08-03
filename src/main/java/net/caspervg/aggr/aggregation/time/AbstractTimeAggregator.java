package net.caspervg.aggr.aggregation.time;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

/**
 * Groups the measurements into multiple sets. Each set is defined by a start- and end time, and will
 * contain only measurements in between those times. The boundaries are calculated by dividing the
 * total duration (between the first and the last measurement) into sections of identical length. Depending
 * on the {@link AbstractTimeAggregator#DETAIL_PARAM}, multiple levels may be created.
 */
public abstract class AbstractTimeAggregator extends AbstractAggregator<TimeAggregation, Measurement> implements TimeAggregator {
    public static final String DETAIL_PARAM = "detail";

    protected static final String DEFAULT_NUM_DETAIL = "8";

}
