package net.caspervg.aggr.worker.diff;

import net.caspervg.aggr.worker.core.Aggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AverageAggregation;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;

public interface DiffAggregator extends Aggregator<DiffAggregation, Measurement> {
}
