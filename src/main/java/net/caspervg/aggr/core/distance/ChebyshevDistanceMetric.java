package net.caspervg.aggr.core.distance;

/**
 * Implementation of the {@link DistanceMetric} interface that uses the
 * <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">Chebyshev distance</a> metric.
 *
 * @param <T> Type of the vectors to calculate distance between
 */
public class ChebyshevDistanceMetric<T extends Number> extends AbstractDistanceMetric<T> {
    @Override
    public double distance(T[] vector1, T[] vector2) {
        super.checkArguments(vector1, vector2);

        double maxDimensionDistance = Double.MIN_VALUE;
        for (int i = 0; i < vector1.length; i++) {
            double p = vector1[i].doubleValue();
            double q = vector2[i].doubleValue();

            double dimensionDistance = Math.abs(p - q);

            if (dimensionDistance > maxDimensionDistance) {
                maxDimensionDistance = dimensionDistance;
            }
        }

        return maxDimensionDistance;
    }
}
