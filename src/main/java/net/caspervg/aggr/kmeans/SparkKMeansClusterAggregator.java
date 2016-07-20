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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import java.io.Serializable;
import java.util.*;

public class SparkKMeansClusterAggregator extends AbstractKMeansAggregator implements Serializable {
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
        int numClusters = Integer.parseInt(
                context.getParameters().getOrDefault(CENTROIDS_PARAM, DEFAULT_NUM_CENTROIDS)
        );

        List<Measurement> measurementList = Lists.newArrayList(measurements);
        JavaRDD<Measurement> measRDD = context.getSparkContext().parallelize(measurementList);

        JavaRDD<Vector> vecRDD = measRDD.map(new Function<Measurement, Vector>() {
            @Override
            public Vector call(Measurement meas) throws Exception {
                return Vectors.dense(ArrayUtils.toPrimitive(meas.getPoint().getVector()));
            }
        });
        vecRDD.cache();

        KMeansModel clusters = KMeans.train(vecRDD.rdd(), numClusters, maxIterations);

        System.out.println("Trained clusters");

        Vector[] centers = clusters.clusterCenters();
        List<Integer> predictedIndices = clusters.predict(vecRDD).collect();

        System.out.println("Predicted indices");

        List<Set<Measurement>> centroidParentsList = new ArrayList<>();
        for (Vector ignored : centers) {
            centroidParentsList.add(new HashSet<>());
        }

        System.out.println("Created parents list");

        for (int i = 0; i < predictedIndices.size(); i++) {
            int predictedIndex = predictedIndices.get(i);
            Set<Measurement> parents = centroidParentsList.get(predictedIndex);
            parents.add(measurementList.get(i));
        }

        System.out.println("Added parents");

        List<Centroid> centroidList = new ArrayList<>();
        for (int i = 0; i < centroidParentsList.size(); i++) {
            Point center = new Point(ArrayUtils.toObject(centers[i].toArray()));
            centroidList.add(new Centroid(center, centroidParentsList.get(i)));
        }

        System.out.println("Added centroids");

        // Return the result of the aggregation
        KMeansAggregation aggr = new KMeansAggregation(
                dataset,
                numClusters,
                maxIterations,
                measurementList
        );
        aggr.setComponents(centroidList);

        return Lists.newArrayList(
                new AggregationResult<>(
                        aggr,
                        centroidList
                )
        );
    }
}
