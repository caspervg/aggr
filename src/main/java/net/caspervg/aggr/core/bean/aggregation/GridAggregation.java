package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class GridAggregation extends AbstractAggregation implements Serializable {

    private double gridSize;

    public GridAggregation(Dataset dataset, Collection<Measurement> source) {
        this(dataset, 0.0005, source);
    }

    public GridAggregation(Dataset dataset, double gridSize, Collection<Measurement> source) {
        this(UUID.randomUUID().toString(), dataset, gridSize,  source);
    }

    public GridAggregation(String uuid, Dataset dataset, double gridSize, Collection<Measurement> source) {
        super(uuid, dataset, source, AggregationType.GRID);
        this.gridSize = gridSize;
    }

    public double getGridSize() {
        return gridSize;
    }
}
