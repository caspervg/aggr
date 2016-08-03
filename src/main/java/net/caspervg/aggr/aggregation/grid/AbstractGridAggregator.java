package net.caspervg.aggr.aggregation.grid;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

/**
 * Rounds the vector of each measurement to a grid. The sensitivity of the grid can be set using the {@link #GRID_SIZE_PARAM}
 * parameter.
 */
public abstract class AbstractGridAggregator extends AbstractAggregator<GridAggregation, Measurement> implements GridAggregator {
    public static final String GRID_SIZE_PARAM = "grid_size";

    protected static final String DEFAULT_GRID_SIZE = "0.0005";
}
