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
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PlainKMeansAggregator extends AbstractKMeansAggregator {

    private DistanceMetric<Double> distanceMetric;

    @Override
    public Iterable<AggregationResult<KMeansAggregation, Measurement>> aggregate(Dataset dataset,
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

        List<Measurement> centroids = StreamSupport.stream(measurements.spliterator(), false)
                .limit(numCentroids)
                .collect(Collectors.toList());

        int iterations = 0;
        while(iterations < maxIterations) {
            Map<Double[], Set<Measurement>> newMapping = new HashMap<>();

            for (Measurement centroid : centroids) {
                newMapping.put(centroid.getVector(), new HashSet<>());
            }

            for (Measurement measurement : measurements) {
                Double[] closestCentroidVector = closestCentroidVector(centroids, measurement);

                if (newMapping.containsKey(closestCentroidVector)) {
                    newMapping.get(closestCentroidVector).add(measurement);
                } else {
                    throw new AssertionError("This shouldn't happen");
                }
            }

            centroids = new ArrayList<>(centroids.size());
            for (Map.Entry<Double[], Set<Measurement>> entry : newMapping.entrySet()) {
                Measurement nextCentroid = context.newOutputMeasurement();
                Double[] current = entry.getKey();
                Set<Measurement> parents = entry.getValue();

                nextCentroid.setVector(optimalPosition(current, parents));
                nextCentroid.setParents(Sets.newHashSet(parents));
                nextCentroid.setDatum(WeightedGeoMeasurement.WEIGHT_KEY, parents.size());

                centroids.add(nextCentroid);
            }

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

    private Double[] closestCentroidVector(List<Measurement> centroids, Measurement measurement) {
        Measurement currentClosest = centroids.get(0);
        double minimumDistance = this.distanceMetric.distance(
                currentClosest.getVector(),
                measurement.getVector()
        );

        for (Measurement possibleClosest : centroids) {
            double possibleDistance = this.distanceMetric.distance(
                    possibleClosest.getVector(),
                    measurement.getVector()
            );

            if (possibleDistance < minimumDistance) {
                minimumDistance = possibleDistance;
                currentClosest = possibleClosest;
            }
        }

        return currentClosest.getVector();
    }

    private Double[] optimalPosition(Double[] current, Set<Measurement> parents) {
        if (parents.size() == 0) return current;
        double[] sum = null;

        for (Measurement parent : parents) {
            if (sum == null) {
                sum = new double[parent.getVector().length];
            }

            for (int i = 0; i < sum.length; i++) {
                sum[i] += parent.getVector()[i];
            }
        }

        assert sum != null;
        double[] avg = new double[sum.length];
        for (int i = 0; i < avg.length; i++) {
            avg[i] = sum[i] / parents.size();
        }

        return ArrayUtils.toObject(avg);
    }
}
