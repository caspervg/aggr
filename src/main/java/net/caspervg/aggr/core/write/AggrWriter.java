package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

public interface AggrWriter {
    void writeMeasurement(Measurement measurement, AggrContext context);
    void writeMeasurements(Iterable<Measurement> measurements, AggrContext context);
    void writeCentroid(Centroid centroid, AggrContext context);
    void writeCentroids(Iterable<Centroid> centroids, AggrContext context);
    void writeAggregation(TimeAggregation aggregation, AggrContext context);
    void writeAggregation(KMeansAggregation aggregation, AggrContext context);
    void writeAggregation(GridAggregation aggregation, AggrContext context);
    void writeDataset(Dataset dataset, AggrContext context);
}
