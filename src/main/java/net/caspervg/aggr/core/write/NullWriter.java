package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

/**
 * Implementation of the {@link AggrWriter} interface that doesn't write anything.
 * Could be useful if you don't care about certain results (e.g. metadata)
 */
public class NullWriter implements AggrWriter {
    @Override
    public void writeMeasurement(Measurement measurement, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeMeasurements(Iterable<Measurement> measurements, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeCentroid(Centroid centroid, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeCentroids(Iterable<Centroid> centroids, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeAggregation(TimeAggregation aggregation, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeAggregation(KMeansAggregation aggregation, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeAggregation(GridAggregation aggregation, AggrContext context) {
        // Empty on purpose, don't write anything
    }

    @Override
    public void writeDataset(Dataset dataset, AggrContext context) {
        // Empty on purpose, don't write anything
    }
}
