package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Aggregation for projecting measurements onto a grid (rounding)
 */
public class GridAggregation extends AbstractAggregation implements Serializable {

    private double gridSize;

    /**
     * Creates a new GridAggregation with a default grid size of 0.0005. A UUID will be generated.
     *
     * @param dataset Dataset to use
     * @param source Source data
     */
    public GridAggregation(Dataset dataset, Collection<Measurement> source) {
        this(dataset, 0.0005, source);
    }

    /**
     * Creates a new GridAggregation with given grid size. A UUID will be generated.
     *
     * @param dataset Dataset to use
     * @param gridSize Grid size that was used
     * @param source Source data
     */
    public GridAggregation(Dataset dataset, double gridSize, Collection<Measurement> source) {
        this(UUID.randomUUID().toString(), dataset, gridSize,  source);
    }

    /**
     * Creates a new GridAggregation with given grid size and UUID.
     *
     * @param uuid UUID to use
     * @param dataset Dataset to use
     * @param gridSize Grid size that was used
     * @param source Source data
     */
    public GridAggregation(String uuid, Dataset dataset, double gridSize, Collection<Measurement> source) {
        super(uuid, dataset, source, AggregationType.GRID);
        this.gridSize = gridSize;
    }

    /**
     * Returns the grid size that was used for projecting/rounding
     *
     * @return the grid size
     */
    public double getGridSize() {
        return gridSize;
    }
}
