package net.caspervg.aggr.worker.grid;

import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.GridAggregation;

public abstract class AbstractGridAggregator extends AbstractAggregator<GridAggregation, Measurement> implements GridAggregator {
    public static final String GRID_SIZE_PARAM = "grid_size";

    protected static final String DEFAULT_GRID_SIZE = "0.0005";
}
