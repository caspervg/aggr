package net.caspervg.aggr.worker.core.bean.aggregation;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.Collection;
import java.util.UUID;

public class DiffAggregation extends AbstractAggregation {

    private String subtrahend;

    public DiffAggregation(Dataset dataset, String subtrahend, Collection<Measurement> sources, Collection<Measurement> results) {
        this(UUID.randomUUID().toString(), dataset, subtrahend, sources, results);
    }

    public DiffAggregation(String uuid, Dataset dataset, String subtrahend, Collection<Measurement> sources, Collection<Measurement> results) {
        super(uuid, dataset, sources, results);
        this.subtrahend = subtrahend;
    }

    public String getSubtrahend() {
        return subtrahend;
    }
}
