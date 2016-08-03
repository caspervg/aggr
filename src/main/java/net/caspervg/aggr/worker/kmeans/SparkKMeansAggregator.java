package net.caspervg.aggr.worker.kmeans;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.worker.core.bean.impl.WeightedGeoMeasurement;
import net.caspervg.aggr.worker.core.distance.DistanceMetric;
import net.caspervg.aggr.worker.core.distance.DistanceMetricChoice;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.util.*;

public class SparkKMeansAggregator extends AbstractKMeansAggregator {

    @Override
    public Iterable<AggregationResult<KMeansAggregation, Measurement>> aggregate(Dataset dataset,
                                                                              Iterable<Measurement> measurements,
                                                                              AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        Class<? extends Measurement> clazz = context.getOutputClass();

        DistanceMetric<Double> distanceMetric = DistanceMetricChoice.valueOf(
                context.getParameters().getOrDefault(METRIC_PARAM, DEFAULT_DISTANCE_METRIC)
        ).getMetric();

        int maxIterations = Integer.parseInt(
                context.getParameters().getOrDefault(ITERATIONS_PARAM, DEFAULT_MAX_ITERATIONS)
        );
        int numCentroids = Integer.parseInt(
                context.getParameters().getOrDefault(CENTROIDS_PARAM, DEFAULT_NUM_CENTROIDS)
        );

        JavaRDD<Measurement> measRDD = context.getSparkContext().parallelize(Lists.newArrayList(measurements));

        // Choose a number of measurements to act as first generation centroids
        List<Measurement> centroidSeeds = measRDD.takeSample(false, numCentroids);

        // Parallelize the measurements
        JavaRDD<Measurement> centroids = context.getSparkContext().parallelize(centroidSeeds);

        int iterations = 0;
        while (iterations++ < maxIterations) {
            // Find the closest centroid for each measurement
            JavaPairRDD<Measurement, Measurement> closest = measRDD.mapToPair(
                    new SparkClosestCentroidStep(centroids.collect(), distanceMetric)
            );

            centroids = closest
                    .mapValues((Function<Measurement, Tuple2<Measurement, Integer>>) measurement ->
                            // Convert each measurement to a tuple of itself and a count of one
                            // The count will be summed and is then used to calculate the total number of citizens
                            new Tuple2<>(measurement, 1)
                    )
                    .reduceByKey((Function2<Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>>) (pair1, pair2) -> {
                        // Sum the vector of all citizens
                        Double[] vec1 = pair1._1.getVector();
                        Double[] vec2 = pair2._1.getVector();

                        Double[] sum = new Double[vec1.length];
                        for (int i = 0; i < sum.length; i++) {
                            sum[i] = vec1[i] + vec2[i];
                        }

                        Measurement sumMeas = newInstance(clazz);
                        sumMeas.setVector(sum);

                        return new Tuple2<>(sumMeas, pair1._2 + pair2._2);
                    }).map((Function<Tuple2<Measurement, Tuple2<Measurement, Integer>>, Measurement>) centroidTuple2Tuple2 -> {
                        // Calculate the new position of the centroid (average of the citizen measurements)
                        Tuple2<Measurement, Integer> pair = centroidTuple2Tuple2._2;
                        Measurement sum = pair._1;
                        Integer amount = pair._2;

                        Measurement avgMeas = newInstance(clazz);

                        Double[] vecSum = sum.getVector();
                        Double[] vecAvg = new Double[vecSum.length];
                        for (int i = 0; i < vecSum.length; i++) {
                            vecAvg[i] = vecSum[i] / (double) amount;
                        }

                        avgMeas.setVector(vecAvg);

                        return avgMeas;
                    });
        }

        // After the iterations, do a final step that will map the initial measurements to their closest centroids
        JavaPairRDD<Measurement, Iterable<Measurement>> results = measRDD
                .mapToPair(
                        new SparkClosestCentroidStep(centroids.collect(), distanceMetric)
                ).groupByKey();

        Map<Measurement, Iterable<Measurement>> resultMapping = results.collectAsMap();
        List<Measurement> finalCentroids = new ArrayList<>();
        for (Measurement centroid : resultMapping.keySet()) {
            Measurement finalCentroid = context.newInputMeasurement();
            finalCentroid.setVector(centroid.getVector());
            Set<Measurement> parents = Sets.newHashSet(resultMapping.get(centroid));
            finalCentroid.setParents(Sets.newHashSet(parents));
            finalCentroid.setDatum(WeightedGeoMeasurement.WEIGHT_KEY, parents.size());

            finalCentroids.add(
                finalCentroid
            );
        }

        // Return the result of the aggregation
        KMeansAggregation aggr = new KMeansAggregation(
                dataset,
                numCentroids,
                maxIterations,
                Lists.newArrayList(measurements),
                finalCentroids
        );
        aggr.setComponents(finalCentroids);

        return Lists.newArrayList(
                new AggregationResult<>(
                        aggr,
                        finalCentroids
                )
        );
    }
}
