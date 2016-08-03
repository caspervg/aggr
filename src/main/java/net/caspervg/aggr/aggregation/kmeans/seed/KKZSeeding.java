package net.caspervg.aggr.aggregation.kmeans.seed;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.distance.DistanceMetric;
import net.caspervg.aggr.core.distance.EuclideanDistanceMetric;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


public class KKZSeeding implements SeedingStrategy {

    private final DistanceMetric<Double> metric = new EuclideanDistanceMetric<>();

    /**
     * {@inheritDoc}
     *
     * Calculates the initial seeds using method proposed by <b>K</b>atsavounidis, <b>K</b>ay and <b>Z</b>hang in 1994, as part of
     * the article "A New Initialization Technique for Generalized Lloyd Iteration"
     *
     * @see <a href='https://www.researchgate.net/publication/3342050_A_New_Initialization_Technique_for_Generalized_Lloyd_Iteration'>
     *     IEEE Signal Processing Letters 1(10):144 - 146</a>
     * @param measurements {@inheritDoc}
     * @param n {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int n) {
        Set<Measurement> centroids = new HashSet<>();
        List<Measurement> measurementList = new ArrayList<>(measurements);

        /*
         * Calculate the norms of all vectors in the training set.
         */
        List<Double> norms = measurementList
                .stream()
                .map(Measurement::getVector)
                .map(ArrayRealVector::new)
                .map(RealVector::getNorm)
                .collect(toList());

        /*
         * Choose the vector with the maximum norm as the first codeword
         */
        int highestNormIndex = IntStream.range(0, norms.size())
                .boxed()
                .max(Comparator.comparing(norms::get))
                .orElseThrow(() -> new IllegalArgumentException("Need at least one measurement"));
        centroids.add(measurementList.remove(highestNormIndex));


        /*
         * The procedure stops if we obtain a codebook of size N
         */
        while (centroids.size() < n) {
            /*
             * Calculate the distance of all training vectors from the existing choices
             */
            List<Double> smallestDistances = measurementList
                    .stream()
                    .map(value -> smallestDistance(centroids, value))
                    .collect(Collectors.toList());

            /*
             * The training vector with the largest distance from the codebook is chosen
             * to be the (i+1)th codeword.
             */
            int highestDistanceIndex = IntStream.range(0, norms.size())
                    .boxed()
                    .max(Comparator.comparing(smallestDistances::get))
                    .orElseThrow(IllegalStateException::new);
            centroids.add(measurementList.remove(highestDistanceIndex));
        }

        return centroids;
    }

    private double smallestDistance(Collection<Measurement> centroids, Measurement meas) {
        double smallestDistance = Double.MAX_VALUE;
        for (Measurement point : centroids) {
            double distance = metric.distance(point.getVector(), meas.getVector());
            if (distance < smallestDistance) {
                smallestDistance = distance;
            }
        }
        return smallestDistance;
    }
}
