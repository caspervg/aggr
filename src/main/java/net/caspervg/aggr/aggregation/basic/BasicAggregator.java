package net.caspervg.aggr.aggregation.basic;

import net.caspervg.aggr.aggregation.Aggregator;
import net.caspervg.aggr.core.bean.Measurement;

/**
 * Performs a basic aggregation that requires no additional parameters.
 *
 * @see net.caspervg.aggr.aggregation.basic.combination.PlainCombinationAggregator
 */
public interface BasicAggregator extends Aggregator<BasicAggregation, Measurement> {
}
