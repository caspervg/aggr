package net.caspervg.aggr.worker.kmeans;

import net.caspervg.aggr.worker.core.AbstractAggregator;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.KMeansAggregation;

public abstract class AbstractKMeansAggregator extends AbstractAggregator<KMeansAggregation, Measurement> implements KMeansAggregator {
    public static final String METRIC_PARAM = "metric";
    public static final String CENTROIDS_PARAM = "num_centroids";
    public static final String ITERATIONS_PARAM = "max_iterations";

    protected static final String DEFAULT_DISTANCE_METRIC = "EUCLIDEAN";
    protected static final String DEFAULT_MAX_ITERATIONS = "50";
    protected static final String DEFAULT_NUM_CENTROIDS = "25";
}
