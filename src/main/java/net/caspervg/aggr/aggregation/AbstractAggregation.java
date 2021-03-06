package net.caspervg.aggr.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Abstract class that contains some useful properties for aggregation beans
 */
public abstract class AbstractAggregation implements Serializable {

    private String uuid;
    private Dataset dataset;
    private Collection<Measurement> sources;
    private Collection<? extends UniquelyIdentifiable> components;
    private String dataPath = "sparql";
    private LocalDateTime created = LocalDateTime.now();

    public AbstractAggregation(Dataset dataset, Collection<Measurement> sources, Collection<Measurement> results) {
        this(UUID.randomUUID().toString(), dataset, sources, results);
    }

    public AbstractAggregation(String uuid, Dataset dataset, Collection<Measurement> sources, Collection<Measurement> results) {
        this.uuid = uuid;
        this.dataset = dataset;
        this.sources = sources;
        this.components = new ArrayList<>(results);
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

    public LocalDateTime getCreated() {
        return created;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * Return the measurements that were used as source for this aggregation
     *
     * @return Source measurements
     */
    public Collection<Measurement> getSources() {
        return sources;
    }

    public Collection<? extends UniquelyIdentifiable> getComponents() {
        return components;
    }

    public void setComponents(Collection<? extends UniquelyIdentifiable> components) {
        this.components = components;
    }
}
