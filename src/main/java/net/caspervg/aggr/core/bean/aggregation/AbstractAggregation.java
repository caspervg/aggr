package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public abstract class AbstractAggregation implements Serializable {

    private String uuid;
    private Dataset dataset;
    private Collection<Measurement> sources;
    private AggregationType aggregationType;

    public AbstractAggregation(AggregationType type, Dataset dataset, Collection<Measurement> sources) {
        this(UUID.randomUUID().toString(), dataset, sources, type);
    }

    public AbstractAggregation(String uuid, Dataset dataset, Collection<Measurement> sources, AggregationType type) {
        this.uuid = uuid;
        this.dataset = dataset;
        this.sources = sources;
        this.aggregationType = type;
    }

    public String getUuid() {
        return uuid;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public Collection<Measurement> getSources() {
        return sources;
    }
}
