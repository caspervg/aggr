package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

public interface AggrResultWriter {
    void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context);
    void writeKMeansAggregation(AggregationResult<KMeansAggregation, Centroid> result, AggrContext context);
    void writeTimeAggregation(AggregationResult<TimeAggregation, Measurement> result, AggrContext context);
    void writeDataset(Dataset dataset, AggrContext context);
}
