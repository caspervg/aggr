package net.caspervg.aggr.kmeans;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.distance.DistanceMetric;
import net.caspervg.aggr.core.distance.DistanceMetricChoice;
import net.caspervg.aggr.core.util.AggrContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.time.LocalDateTime;
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

        List<Measurement> centroidSeeds = measRDD.takeSample(false, numCentroids);
        JavaRDD<Centroid> centroids = context.getSparkContext().parallelize(centroidSeeds).map(
                (Function<Measurement, Centroid>) measurement ->
                        new Centroid(
                                new Double[]{
                                        measurement.getPoint().getVector()[0],
                                        measurement.getPoint().getVector()[1]
                                },
                                new HashSet<>()
                        )
        );

        int iterations = 0;
        while(iterations++ < maxIterations) {
            JavaPairRDD<Centroid, Measurement> closest = measRDD.mapToPair(new SparkClosestCentroidStep(centroids.collect(), distanceMetric));

            centroids = closest
                    .mapValues((Function<Measurement, Tuple2<Measurement, Integer>>) measurement ->
                            new Tuple2<>(measurement, 1)
                    ).reduceByKey((Function2<Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>, Tuple2<Measurement, Integer>>) (pair1, pair2) -> {
                        Point sum = new Point(new Double[]{
                                pair1._1.getPoint().getVector()[0] + pair2._1.getPoint().getVector()[0],
                                pair1._1.getPoint().getVector()[1] + pair2._1.getPoint().getVector()[1]
                        });

                        Measurement sumMeas = new Measurement(sum, LocalDateTime.now());
                        return new Tuple2<>(sumMeas, pair1._2 + pair2._2);
                    }).map((Function<Tuple2<Centroid, Tuple2<Measurement, Integer>>, Centroid>) centroidTuple2Tuple2 -> {
                        Tuple2<Measurement, Integer> pair = centroidTuple2Tuple2._2;
                        Measurement sum = pair._1;
                        Integer amount = pair._2;
                        return new Centroid(new Double[]{
                                sum.getPoint().getVector()[0] / amount,
                                sum.getPoint().getVector()[1] / amount
                        }, new HashSet<>());
                    });
        }

        JavaPairRDD<Centroid, Iterable<Measurement>> results = measRDD
                .mapToPair(
                        new SparkClosestCentroidStep(centroids.collect(), distanceMetric)
                ).groupByKey();

        Map<Centroid, Iterable<Measurement>> resultMapping = results.collectAsMap();
        List<Centroid> finalCentroids = new ArrayList<>();
        for (Centroid centroid : resultMapping.keySet()) {
            finalCentroids.add(new Centroid(centroid.getVector(), Sets.newHashSet(
                    resultMapping.get(centroid)
            )));
        }

        return Lists.newArrayList(
                new AggregationResult<>(
                        new KMeansAggregation(
                                dataset,
                                numCentroids,
                                maxIterations,
                                Lists.newArrayList(measurements)
                        ),
                        finalCentroids
                )
        );
    }
}
