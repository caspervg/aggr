package net.caspervg.aggr.aggregation.kmeans.clusters;

public interface ClusterStrategy {
    /**
     * Returns the suggested number of clusters for a k-Means algorithm
     * @return suggested number of clusters
     */
    int clusters();
}
