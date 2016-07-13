package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class KMeansAggregation extends AbstractAggregation implements Serializable {

    private int k;
    private int n;

    public KMeansAggregation(String uuid, Dataset dataset, int k, int n, Collection<Measurement> source) {
        super(uuid, dataset, source, AggregationType.KMEANS);
        this.k = k;
        this.n = n;
    }

    public KMeansAggregation(Dataset dataset, int k, int n, Collection<Measurement> source) {
        this(UUID.randomUUID().toString(), dataset, k, n, source);
    }

    public int getK() {
        return k;
    }

    public int getN() {
        return n;
    }
}
