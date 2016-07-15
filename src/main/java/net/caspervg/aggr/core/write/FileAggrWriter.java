package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

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
    public void writeDataset(Dataset dataset, AggrContext context) {
        notSupported();
    }

    private void notSupported() {
        throw new UnsupportedOperationException("Writing aggegrations or datasets to CSV is not supported");
    }
}
