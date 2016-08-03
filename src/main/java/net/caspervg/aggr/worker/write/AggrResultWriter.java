package net.caspervg.aggr.worker.write;

import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.aggregation.average.AverageAggregation;
import net.caspervg.aggr.aggregation.basic.BasicAggregation;
import net.caspervg.aggr.aggregation.diff.DiffAggregation;
import net.caspervg.aggr.aggregation.grid.GridAggregation;
import net.caspervg.aggr.aggregation.kmeans.KMeansAggregation;
import net.caspervg.aggr.aggregation.time.TimeAggregation;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.util.AggrContext;

public interface AggrResultWriter {
    void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context);
    void writeKMeansAggregation(AggregationResult<KMeansAggregation, Measurement> result, AggrContext context);
    void writeTimeAggregation(AggregationResult<TimeAggregation, Measurement> result, AggrContext context);
    void writeBasicAggregation(AggregationResult<BasicAggregation, Measurement> result, AggrContext context);
    void writeDiffAggregation(AggregationResult<DiffAggregation, Measurement> res, AggrContext ctx);
    void writeAverageAggregation(AggregationResult<AverageAggregation, Measurement> res, AggrContext ctx);
    void writeDataset(Dataset dataset, AggrContext context);
}
