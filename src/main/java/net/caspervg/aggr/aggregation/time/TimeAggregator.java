package net.caspervg.aggr.aggregation.time;

import net.caspervg.aggr.aggregation.Aggregator;
import net.caspervg.aggr.core.bean.Measurement;

/**
 * Groups the measurements into multiple sets, depending on their timestamp.
 *
 * @see Aggregator
 * @see Measurement#getTimestamp()
 */
public interface TimeAggregator extends Aggregator<TimeAggregation, Measurement> {
}
