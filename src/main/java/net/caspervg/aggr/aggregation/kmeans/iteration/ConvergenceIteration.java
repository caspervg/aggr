package net.caspervg.aggr.aggregation.kmeans.iteration;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.distance.DistanceMetric;
import net.caspervg.aggr.core.distance.EuclideanDistanceMetric;
import net.caspervg.aggr.core.bean.util.MeasurementVectorComparator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConvergenceIteration {

    private double epsilon;
    private DistanceMetric<Double> metric = new EuclideanDistanceMetric<>();

    public ConvergenceIteration() {
        this(0.01);
    }

    public ConvergenceIteration(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Checks if the distances between the previous and current generation centroids
     * are smaller than {@code epsilon}. If that is the case, it will call for the k-Means
     * algorithm to stop iterating.
     *
     * @param iterations {@inheritDoc}
     * @param prevCentroids {@inheritDoc}
     * @param nextCentroids {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean shouldContinue(int iterations, List<Measurement> prevCentroids, List<Measurement> nextCentroids) {
        Collections.sort(prevCentroids, new MeasurementVectorComparator());
        Collections.sort(nextCentroids, new MeasurementVectorComparator());

        Iterator<Measurement> iterator = prevCentroids.iterator();

        boolean done = false;
        for (Measurement nextCentroid : nextCentroids) {
            Measurement prevCentroid = iterator.next();
            double distance = metric.distance(nextCentroid.getVector(), prevCentroid.getVector());
            if (distance <= this.epsilon) {
                done = true;
            } else {
                done = false;
                break;
            }
        }

        return !done;
    }
}
