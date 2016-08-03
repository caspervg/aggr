package net.caspervg.aggr.worker.write;

import net.caspervg.aggr.aggregation.average.AverageAggregation;
import net.caspervg.aggr.aggregation.basic.BasicAggregation;
import net.caspervg.aggr.aggregation.diff.DiffAggregation;
import net.caspervg.aggr.aggregation.grid.GridAggregation;
import net.caspervg.aggr.aggregation.kmeans.KMeansAggregation;
import net.caspervg.aggr.aggregation.time.TimeAggregation;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.util.AggrContext;

/**
 * Implementation of the {@link AggrWriter} interface that does not allow
 * writing metadata (aggregations, datasets).
 */
public abstract class FileAggrWriter extends AbstractAggrWriter {
    @Override
    public void writeAggregation(TimeAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeAggregation(KMeansAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeAggregation(GridAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeAggregation(BasicAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeAggregation(DiffAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeAggregation(AverageAggregation aggregation, AggrContext context) {
        notSupported();
    }

    @Override
    public void writeDataset(Dataset dataset, AggrContext context) {
        notSupported();
    }

    private void notSupported() {
        throw new UnsupportedOperationException("Writing aggregations or datasets to CSV is not supported");
    }
}
