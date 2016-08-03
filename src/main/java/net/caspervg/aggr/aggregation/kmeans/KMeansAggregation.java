package net.caspervg.aggr.aggregation.kmeans;

import net.caspervg.aggr.aggregation.AbstractAggregation;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * Aggregation that creates K means based on the data
 */
public class KMeansAggregation extends AbstractAggregation implements Serializable {

    private int k;
    private int n;

    /**
     * Creates a KMeansAggregation with given UUID, number of means and number of iterations
     *
     * @param uuid UUID of the aggregation
     * @param dataset Dataset that was used
     * @param k Number of means
     * @param n Number of iterations
     * @param source Source data
     */
    public KMeansAggregation(String uuid, Dataset dataset, int k, int n, Collection<Measurement> source, Collection<Measurement> results) {
        super(uuid, dataset, source, results);
        this.k = k;
        this.n = n;
    }

    /**
     * Creates a KMeansAggregation with given number of means and number of iterations. An UUID will be generated.
     *
     * @param dataset Dataset that was used
     * @param k Number of means
     * @param n Number of iterations
     * @param source Source data
     */
    public KMeansAggregation(Dataset dataset, int k, int n, Collection<Measurement> source, Collection<Measurement> results) {
        this(UUID.randomUUID().toString(), dataset, k, n, source, results);
    }

    /**
     * Retrieves the number of means in this aggregation
     *
     * @return Number of means
     */
    public int getK() {
        return k;
    }

    /**
     * Retrieves the number of iterations that this aggregation performed
     *
     * @return Number of iterations
     */
    public int getN() {
        return n;
    }
}
