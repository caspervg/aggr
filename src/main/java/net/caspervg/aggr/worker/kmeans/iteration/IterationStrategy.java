package net.caspervg.aggr.worker.kmeans.iteration;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.List;

public interface IterationStrategy {
    /**
     * Checks if the k-Means algorithm should continue iterating
     *
     * @param iteration Current iteration number
     * @param prevCentroids Centroids that were calculated in the previous iteration
     * @param nextCentroids Centroids that were calculated in the current iteration
     * @return {@code true} if the k-Means algorithm should continue into the next iteration
     *         {@code false} otherwise
     * @throws IllegalArgumentException if the centroid collections are empty
     */
    boolean shouldContinue(int iteration, List<Measurement> prevCentroids, List<Measurement> nextCentroids);
}
