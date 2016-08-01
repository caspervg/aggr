package net.caspervg.aggr.worker.core.write;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.*;
import net.caspervg.aggr.worker.core.util.AggrContext;

public interface AggrResultWriter {
    void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context);
    void writeKMeansAggregation(AggregationResult<KMeansAggregation, Measurement> result, AggrContext context);
    void writeTimeAggregation(AggregationResult<TimeAggregation, Measurement> result, AggrContext context);
    void writeBasicAggregation(AggregationResult<BasicAggregation, Measurement> result, AggrContext context);
    void writeDiffAggregation(AggregationResult<DiffAggregation, Measurement> res, AggrContext ctx);
    void writeDataset(Dataset dataset, AggrContext context);
}
