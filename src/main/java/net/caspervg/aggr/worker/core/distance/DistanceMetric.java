package net.caspervg.aggr.worker.core.distance;

/**
 * This interface provides a method to calculate the distance between vectors
 * with some metric.
 *
 * @param <T> Type of vectors to calculate distance between
 */
@FunctionalInterface
public interface DistanceMetric<T extends Number> {
    /**
     * Calculates the distance between two vectors using some metric.
     *
     * @param vector1 First vector to calculate distance with
     * @param vector2 Second vector to calculate distance with
     * @return Distance between two vectors using some metric
     */
    double distance(T[] vector1, T[] vector2);
}
