package net.caspervg.aggr.aggregation.grid;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.core.bean.Measurement;

public abstract class AbstractGridAggregator extends AbstractAggregator<GridAggregation, Measurement> implements GridAggregator {
    public static final String GRID_SIZE_PARAM = "grid_size";

    protected static final String DEFAULT_GRID_SIZE = "0.0005";
}
