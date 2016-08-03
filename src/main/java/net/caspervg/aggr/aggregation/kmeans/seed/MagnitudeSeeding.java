package net.caspervg.aggr.aggregation.kmeans.seed;

import net.caspervg.aggr.core.bean.Measurement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.stream.IntStream;

public class MagnitudeSeeding implements SeedingStrategy {
    /**
     * {@inheritDoc}
     *
     * Calculates the initial seeds using method proposed by Fouad Khan in 2012, as part of
     * the article "An initial seed selection algorithm for k-means clustering of georeferenced data
     * to improve repeatability of cluster assignments for mapping applications"
     *
     * @see <a href='http://www.sciencedirect.com/science/article/pii/S1568494612003377'>doi:10.1016/j.asoc.2012.07.021</a>
     * @param measurements {@inheritDoc}
     * @param n {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int n) {
        List<Measurement> measurementList = new ArrayList<>(measurements);

        /*
         * Sort the data points in terms of increasing magnitude d_1, ..., d_n such that
         * d_1 has the minimum and d_n has the maximum magnitude.
         */
        double[] norms = measurementList
                .stream()
                .map(Measurement::getVector)
                .map(ArrayRealVector::new)
                .mapToDouble(RealVector::getNorm)
                .toArray();
        Arrays.sort(norms);                                                         // Ascending order

        /*
         * Calculate the Euclidean distances D_i between consecutive points d_i and d_{i+1} as shown:
         * D_i = d_{i+1} - d_{i}
         */
        double[] distances = new double[norms.length - 1];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = norms[i+1] - norms[i];
        }

        /*
         * Sort D in descending order without changing the index i of each D_i.
         * Identify k − 1 index i values (i_1, … , i_{k−1}) that correspond to the k − 1 highest D_i values.
         */
        int[] indices = IntStream.range(0, distances.length)
                .boxed()
                .sorted((i, j) -> Double.compare(distances[j], distances[i]))       // Descending order
                .mapToInt(el -> el)
                .toArray();

        /*
         * Sort i_1, …, i_{k−1} in ascending order.
         */
        int[] highestDistanceIndices = Arrays.copyOfRange(indices, 1, n - 1);
        Arrays.sort(highestDistanceIndices);                                        // Ascending order

        /*
         * The set (i_1, …, i_{k−1}, i_{k}) now forms the set of indices
         * of data values d_i, which serve as the upper bounds of clusters 1, …, k; where i_k = n.
         */
        int[] upperBounds = ArrayUtils.add(highestDistanceIndices, indices[n]);

        /*
         * The corresponding set of indices of data values d_i which serve as the lower bounds of clusters 1, …, k
         * would simply be defined as (i0, i1 + 1, …, i(k−1) + 1), where i0 = 1.
         */
        int[] lowerBounds = new int[upperBounds.length];
        lowerBounds[0] = 1;
        for (int j = 1; j < lowerBounds.length; j++) {
            lowerBounds[j] = highestDistanceIndices[j-1] + 1;
        }

        // TODO: actually calculate the clusters
        return null;
    }
}
