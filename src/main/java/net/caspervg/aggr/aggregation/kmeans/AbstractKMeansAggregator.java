package net.caspervg.aggr.aggregation.kmeans;

import net.caspervg.aggr.aggregation.AbstractAggregator;
import net.caspervg.aggr.aggregation.time.AbstractTimeAggregator;
import net.caspervg.aggr.core.bean.Measurement;

/**
 * Performs a k-Means algorithm to assign the measurements to a centroid. The number of centroids to be created
 * is defined by the {@link #CENTROIDS_PARAM}, while the maximum number of iterations to use is defined in
 * {@link #ITERATIONS_PARAM}. One can also set the distance metric to use, using the {@link #METRIC_PARAM}.
 */
public abstract class AbstractKMeansAggregator extends AbstractAggregator<KMeansAggregation, Measurement> implements KMeansAggregator {
    public static final String METRIC_PARAM = "metric";
    public static final String CENTROIDS_PARAM = "num_centroids";
    public static final String ITERATIONS_PARAM = "max_iterations";

    protected static final String DEFAULT_DISTANCE_METRIC = "EUCLIDEAN";
    protected static final String DEFAULT_MAX_ITERATIONS = "50";
    protected static final String DEFAULT_NUM_CENTROIDS = "25";
}
