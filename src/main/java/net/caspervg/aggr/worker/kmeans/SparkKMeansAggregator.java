package net.caspervg.aggr.worker.kmeans;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.worker.core.bean.Centroid;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.Point;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.KMeansAggregation;
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
    public Iterable<AggregationResult<KMeansAggregation, Centroid>> aggregate(Dataset dataset,
                                                                              Iterable<Measurement> measurements,
                                                                              AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

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

        // Convert these measurements into Centroid objects
        JavaRDD<Centroid> centroids = context.getSparkContext().parallelize(centroidSeeds).map(
                (Function<Measurement, Centroid>) measurement -> {
                    return Centroid.Builder
                            .setup()
                            .withPoint(
                                    new Point(
                                            new Double[]{
                                                    measurement.getPoint().getVector()[0],
                                                    measurement.getPoint().getVector()[1]
                                            }
                                    )
                            )
                            .build();
                }
        );

        int iterations = 0;
        while(iterations++ < maxIterations) {
            // Find the closest centroid for each measurement
            JavaPairRDD<Centroid, Measurement> closest = measRDD.mapToPair(new SparkClosestCentroidStep(centroids.collect(), distanceMetric));

            centroids = closest
                    .mapValues((Function<Measurement, Tuple2<Measurement, Integer>>) measurement ->
                            // Convert each measurement to a tuple of itself and a count of one
                            // The count will be summed and is then used to calculate the total number of citizens
                            new Tuple2<>(measurement, 1)
                    ).reduceByKey((Function2<Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>>) (pair1, pair2) -> {
                        // Sum the locations of all citizens
                        Point sum = new Point(new Double[]{
                                pair1._1.getPoint().getVector()[0] + pair2._1.getPoint().getVector()[0],
                                pair1._1.getPoint().getVector()[1] + pair2._1.getPoint().getVector()[1]
                        });

                        Measurement sumMeas = Measurement.Builder
                                .setup()
                                .withPoint(sum)
                                .build();

                        return new Tuple2<>(sumMeas, pair1._2 + pair2._2);
                    }).map((Function<Tuple2<Centroid, Tuple2<Measurement, Integer>>, Centroid>) centroidTuple2Tuple2 -> {
                        // Calculate the new position of the centroid (average of the citizen measurements)
                        Tuple2<Measurement, Integer> pair = centroidTuple2Tuple2._2;
                        Measurement sum = pair._1;
                        Integer amount = pair._2;

                        return Centroid.Builder
                                .setup()
                                .withPoint(
                                        new Point(
                                                new Double[]{
                                                        sum.getPoint().getVector()[0] / amount,
                                                        sum.getPoint().getVector()[1] / amount
                                                }
                                        )
                                )
                                .build();
                    });
        }

        // After the iterations, do a final step that will map the initial measurements to their closest centroids
        JavaPairRDD<Centroid, Iterable<Measurement>> results = measRDD
                .mapToPair(
                        new SparkClosestCentroidStep(centroids.collect(), distanceMetric)
                ).groupByKey();

        Map<Centroid, Iterable<Measurement>> resultMapping = results.collectAsMap();
        List<Centroid> finalCentroids = new ArrayList<>();
        for (Centroid centroid : resultMapping.keySet()) {
            finalCentroids.add(
                    Centroid.Builder
                            .setup()
                            .withPoint(centroid.getPoint())
                            .withParents(Sets.newHashSet(resultMapping.get(centroid)))
                            .build()
            );
        }

        // Return the result of the aggregation
        KMeansAggregation aggr = new KMeansAggregation(
                dataset,
                numCentroids,
                maxIterations,
                Lists.newArrayList(measurements)
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
