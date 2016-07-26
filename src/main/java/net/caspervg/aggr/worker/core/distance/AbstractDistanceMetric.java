package net.caspervg.aggr.worker.core.distance;

import java.io.Serializable;

/**
 * Abstract implementation of the {@link DistanceMetric} interface that provides a
 * method to easily check for the required arguments.
 *
 * @param <T> Type of the vectors to calculate distance between
 */
abstract class AbstractDistanceMetric<T extends Number> implements DistanceMetric<T>, Serializable {
    /**
     * Checks if both arguments are non-null and if their lengths are equal
     *
     * @param vector1 First vector
     * @param vector2 Second vector
     */
    void checkArguments(T[] vector1, T[] vector2) {
        if (vector1 == null || vector2 == null) {
            throw new IllegalArgumentException("Both vectors must not be null");
        }

        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Both vectors must have an equal length");
        }
    }
}