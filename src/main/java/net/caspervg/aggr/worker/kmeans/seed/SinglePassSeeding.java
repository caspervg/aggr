package net.caspervg.aggr.worker.kmeans.seed;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.distance.DistanceMetric;
import net.caspervg.aggr.worker.core.distance.EuclideanDistanceMetric;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import java.util.stream.IntStream;

public class SinglePassSeeding implements SeedingStrategy {

    private final DistanceMetric<Double> metric = new EuclideanDistanceMetric<>();

    /**
     * {@inheritDoc}
     *
     * Calculates the initial seeds using method proposed by Pavan et al. in 2010, as part of
     * the article "Single Pass Seed Selection Algorithm for k-Means"
     *
     * @see <a href='http://thescipub.com/PDF/jcssp.2010.60.66.pdf'>jcssp.2010.60.66</a>
     * @param measurements {@inheritDoc}
     * @param numClusters {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int numClusters) {
        List<Measurement> measurementList = new ArrayList<>(measurements);

        int n = measurementList.size();
        int k = numClusters;

        Set<Measurement> centroids = new HashSet<>();

        double[][] dist = new double[measurementList.size()][measurementList.size()];

        /*
         * Calculate distance matrix Dist in which Dist (i,j) represents distance from i to j
         */
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[i].length; j++) {
                dist[i][j] = metric.distance(measurementList.get(i).getVector(), measurementList.get(j).getVector());
            }
        }

        /*
         * Find Sumv in which Sumv[i] is the sum of the distances from ith point to all other points.
         */
        double[] sumv = new double[dist.length];
        for (int i = 0; i < sumv.length; i++) {
            double sum = 0;
            for (int j = 0; j < dist[i].length; j++) {
                sum += dist[i][j];
            }
            sumv[i] = sum;
        }

        /*
         * Find the point i which is min (Sumv) and set Index = i
         */
        int index = IntStream.range(0, sumv.length)
                                .boxed()
                                .min((o1, o2) -> Double.compare(sumv[o1], sumv[o2]))
                                .get();

        /*
         * Add First to C as the first centroid
         */
        centroids.add(measurementList.get(index));

        /*
         * Repeat steps 5-8 until you have k centers
         */
        //noinspection LoopStatementThatDoesntLoop
        while (centroids.size() < numClusters) {
            /*
             * For each point x_i, set D(x_i) to be the distance between x_i and the nearest point in C
            */
            double[] d = new double[measurementList.size()];
            for (int i = 0; i < measurementList.size(); i++) {
                d[i] = smallestDistance(centroids, measurementList.get(i));
            }

            /*
             * Find y as the sum of distances of first n/k nearest points from the Index
             */
            double[] minv = ArrayUtils.clone(dist[index]);
            double y = 0;
            for (int i = 0; i < Math.ceil(n / (double) k); i++) {
                y += minv[i];
            }

            /*
             * Find the unique integer i so that D(x_1)²+D(x_2)²+...+D(x_i)² >= y > D(x_1)²+D(x_2)²+...+D(x_i-1)²
             */
            for (int i = 0; i < d.length - 1; i++) {
                double preSum = 0;
                for (int j = 0; j < i-1; j++) {
                    preSum += d[j]*d[j];
                }

                if (preSum + d[i]*d[i] >= y && y > preSum) {
                    /*
                     * Add x_i to C
                     */
                    centroids.add(measurementList.get(i));
                    break;
                }
            }
        }

        return centroids;
    }

    private double smallestDistance(Set<Measurement> points, Measurement meas) {
        double smallestDistance = Double.MAX_VALUE;
        for (Measurement point : points) {
            double distance = metric.distance(point.getVector(), meas.getVector());
            if (distance < smallestDistance) {
                smallestDistance = distance;
            }
        }
        return smallestDistance;
    }
}
