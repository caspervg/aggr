package net.caspervg.aggr.worker.kmeans;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.distance.DistanceMetric;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;


public class SparkClosestCentroidStep implements PairFunction<Measurement, Measurement, Measurement> {

    private final List<Measurement> centroids;
    private final DistanceMetric<Double> distanceMetric;

    public SparkClosestCentroidStep(List<Measurement> centroids, DistanceMetric<Double> distanceMetric) {
        this.centroids = centroids;
        this.distanceMetric = distanceMetric;
    }

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
