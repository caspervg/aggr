package net.caspervg.aggr.worker.time;

import net.caspervg.aggr.worker.core.Aggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.TimeAggregation;

public interface TimeAggregator extends Aggregator<TimeAggregation, Measurement> {
}
