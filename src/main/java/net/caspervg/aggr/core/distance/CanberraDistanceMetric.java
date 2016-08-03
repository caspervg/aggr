package net.caspervg.aggr.core.distance;

/**
 * Implementation of the {@link DistanceMetric} interface that uses the
 * <a href="https://en.wikipedia.org/wiki/Canberra_distance">Canberra distance</a> metric.
 *
 * @param <T> Type of the vectors to calculate distance between
 */
public class CanberraDistanceMetric<T extends Number> extends AbstractDistanceMetric<T> {
    @Override
    public double distance(T[] vector1, T[] vector2) {
        super.checkArguments(vector1, vector2);

        double sum = 0;
        for (int i = 0; i < vector1.length; i++) {
            double p = vector1[i].doubleValue();
            double q = vector2[i].doubleValue();

            sum += (Math.abs(p - q)) / (Math.abs(p) + Math.abs(q));
        }

        return sum;
    }
}
