package net.caspervg.aggr.kmeans;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.distance.DistanceMetric;
import net.caspervg.aggr.core.distance.DistanceMetricChoice;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PlainKMeansAggregator extends AbstractKMeansAggregator {

    private DistanceMetric<Double> distanceMetric;

    @Override
    public Iterable<AggregationResult<KMeansAggregation, Centroid>> aggregate(Dataset dataset,
                                                                              Iterable<Measurement> measurements,
                                                                              AggrContext context) {
        this.distanceMetric = DistanceMetricChoice.valueOf(
                context.getParameters().getOrDefault(METRIC_PARAM, DEFAULT_DISTANCE_METRIC)
        ).getMetric();

        int maxIterations = Integer.parseInt(
                context.getParameters().getOrDefault(ITERATIONS_PARAM, DEFAULT_MAX_ITERATIONS)
        );
        int numCentroids = Integer.parseInt(
                context.getParameters().getOrDefault(CENTROIDS_PARAM, DEFAULT_NUM_CENTROIDS)
        );

        List<Centroid> centroids = StreamSupport.stream(measurements.spliterator(), false)
                .limit(numCentroids)
                .map(measurement -> Centroid.Builder.setup().withPoint(measurement.getPoint()).build())
                .collect(Collectors.toList());

        int iterations = 0;
        while(iterations < maxIterations) {
            Map<Double[], Set<Measurement>> newMapping = new HashMap<>();

            for (Centroid centroid : centroids) {
                newMapping.put(centroid.getPoint().getVector(), new HashSet<>());
            }

            for (Measurement measurement : measurements) {
                Double[] closestCentroidVector = closestCentroidVector(centroids, measurement);

                if (newMapping.containsKey(closestCentroidVector)) {
                    newMapping.get(closestCentroidVector).add(measurement);
                } else {
                    throw new AssertionError("This shouldn't happen");
                }
            }

            centroids = newMapping
                    .entrySet()
                    .stream()
                    .map(entry -> Centroid.Builder
                                    .setup()
                                    .withPoint(
                                            new Point(
                                                    entry.getKey()
                                            )
                                    )
                                    .withParents(entry.getValue())
                                    .build()
                    )
                    .map(Centroid::recalculatePosition)
                    .collect(Collectors.toList());

            iterations++;
        }

        // Return the result of the aggregation
        KMeansAggregation aggr = new KMeansAggregation(
                dataset,
                numCentroids,
                maxIterations,
                Lists.newArrayList(measurements)
        );
        aggr.setComponents(centroids);

        return Lists.newArrayList(
                new AggregationResult<>(
                        aggr,
                        centroids
                )
        );
    }

    private Double[] closestCentroidVector(List<Centroid> centroids, Measurement measurement) {
        Centroid currentClosest = centroids.get(0);
        double minimumDistance = this.distanceMetric.distance(
                currentClosest.getPoint().getVector(),
                measurement.getPoint().getVector()
        );

        for (Centroid possibleClosest : centroids) {
            double possibleDistance = this.distanceMetric.distance(
                    possibleClosest.getPoint().getVector(),
                    measurement.getPoint().getVector()
            );

            if (possibleDistance < minimumDistance) {
                minimumDistance = possibleDistance;
                currentClosest = possibleClosest;
            }
        }

        return currentClosest.getPoint().getVector();
    }
}
