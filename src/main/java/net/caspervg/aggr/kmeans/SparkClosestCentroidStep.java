package net.caspervg.aggr.kmeans;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.distance.DistanceMetric;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;


public class SparkClosestCentroidStep implements PairFunction<Measurement,Centroid,Measurement> {

    private final List<Centroid> centroids;
    private final DistanceMetric<Double> distanceMetric;

    public SparkClosestCentroidStep(List<Centroid> centroids, DistanceMetric<Double> distanceMetric) {
        this.centroids = centroids;
        this.distanceMetric = distanceMetric;
    }

    @Override
    public Tuple2<Centroid, Measurement> call(Measurement measurement) throws Exception {
        DistanceMetric<Double> distanceMetric = this.distanceMetric;
        Double[] measurementVector = measurement.getPoint().getVector();

        Centroid nearestCentroid = this.centroids.get(0);
        double minimumDistance = Double.MAX_VALUE;

        for (Centroid centroid : this.centroids) {
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
