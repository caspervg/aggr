package net.caspervg.aggr.worker.average;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.AverageAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;

import java.util.HashSet;
import java.util.Set;

public class PlainAverageAggregator extends AbstractAverageAggregator {

    public PlainAverageAggregator(Iterable<Iterable<Measurement>> others) {
        super(others);
    }

    @Override
    public Iterable<AggregationResult<AverageAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
