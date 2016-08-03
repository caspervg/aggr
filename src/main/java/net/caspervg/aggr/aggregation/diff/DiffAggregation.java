package net.caspervg.aggr.aggregation.diff;

import net.caspervg.aggr.aggregation.AbstractAggregation;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.util.Collection;
import java.util.UUID;

public class DiffAggregation extends AbstractAggregation {

    private String other;
    private String key;

    public DiffAggregation(Dataset dataset, String other, String key, Collection<Measurement> sources, Collection<Measurement> results) {
        this(UUID.randomUUID().toString(), dataset, other, key, sources, results);
    }

    public DiffAggregation(String uuid, Dataset dataset, String other, String key, Collection<Measurement> sources, Collection<Measurement> results) {
        super(uuid, dataset, sources, results);
        this.other = other;
        this.key = key;
    }

    public String getOther() {
        return other;
    }

    public String getKey() {
        return key;
    }
}
