package net.caspervg.aggr.aggregation.kmeans;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.distance.DistanceMetric;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

/**
 * Used by {@link SparkKMeansAggregator} to calculate the closest centroid to a measurement
 */
public class SparkClosestCentroidStep implements PairFunction<Measurement, Measurement, Measurement> {

    private final List<Measurement> centroids;
    private final DistanceMetric<Double> distanceMetric;

    /**
     * Creates a new SparkClosestCentroidStep with given parameters
     *
     * @param centroids List of centroids to select from
     * @param distanceMetric Distance metric to use to calculate the closest centroid
     */
    public SparkClosestCentroidStep(List<Measurement> centroids, DistanceMetric<Double> distanceMetric) {
        this.centroids = centroids;
        this.distanceMetric = distanceMetric;
    }

    /**
     * Calculates the closest centroid to this measurement and returns it in the tuple.
     * @param measurement Measurement to calculate closest centroid for
     * @return Tuple containing the centroid and the original measurement
     * @throws Exception
     */
    @Override
    public Tuple2<Measurement, Measurement> call(Measurement measurement) throws Exception {
        DistanceMetric<Double> distanceMetric = this.distanceMetric;
        Double[] measurementVector = measurement.getVector();

        Measurement nearestCentroid = this.centroids.get(0);
        double minimumDistance = Double.MAX_VALUE;

        for (Measurement centroid : this.centroids) {
            Double [] centroidVector = centroid.getVector();

            double dist = distanceMetric.distance(centroidVector, measurementVector);
            if (dist < minimumDistance) {
                nearestCentroid = centroid;
                minimumDistance = dist;
            }
        }

        return new Tuple2<>(nearestCentroid, measurement);
    }
}
