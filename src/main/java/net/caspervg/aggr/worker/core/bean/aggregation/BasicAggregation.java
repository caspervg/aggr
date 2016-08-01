package net.caspervg.aggr.worker.core.bean.aggregation;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;

public class BasicAggregation extends AbstractAggregation implements Serializable {
    public BasicAggregation(Dataset dataset, Collection<Measurement> sources, Collection<Measurement> results) {
        super(dataset, sources, results);
    }

    public BasicAggregation(String uuid, Dataset dataset, Collection<Measurement> sources, Collection<Measurement> results) {
        super(uuid, dataset, sources, results);
    }
}
