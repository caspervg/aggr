package net.caspervg.aggr.worker.combine;

import net.caspervg.aggr.worker.basic.BasicAggregator;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.BasicAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;

@Deprecated
public class SparkCombinationAggregator implements BasicAggregator {
    @Override
    public Iterable<AggregationResult<BasicAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        return new PlainCombinationAggregator().aggregate(dataset, measurements, context);
    }
}
