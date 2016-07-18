package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Abstract class that contains some useful properties for aggregation beans
 */
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

    /**
     * Return the unique identifier of this aggregation
     *
     * @return Unique identifier
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Return the source dataset that this aggregation belongs to
     *
     * @return Source dataset
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Return the type of the aggregation
     *
     * @return Type of aggregation
     */
    public AggregationType getAggregationType() {
        return aggregationType;
    }

    /**
     * Return the measurements that were used as source for this aggregation
     *
     * @return Source measurements
     */
    public Collection<Measurement> getSources() {
        return sources;
    }
}
