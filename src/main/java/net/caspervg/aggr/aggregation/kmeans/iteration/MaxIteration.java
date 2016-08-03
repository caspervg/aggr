package net.caspervg.aggr.aggregation.kmeans.iteration;

import net.caspervg.aggr.core.bean.Measurement;

import java.util.List;

public class MaxIteration implements IterationStrategy {

    private int maxIterations;

    public MaxIteration() {
        this(50);
    }

    public MaxIteration(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Checks if the iteration number is larger than the max number of iterations.
     *
     * @param iteration {@inheritDoc}
     * @param prevCentroids {@inheritDoc}
     * @param nextCentroids {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean shouldContinue(int iteration, List<Measurement> prevCentroids, List<Measurement> nextCentroids) {
        return iteration < maxIterations;
    }

}
