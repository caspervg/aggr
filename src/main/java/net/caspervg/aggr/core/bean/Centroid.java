package net.caspervg.aggr.core.bean;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Result of a KMeans aggregation
 */
public class Centroid extends Measurement implements Serializable, UniquelyIdentifiable {

    public Centroid(Point point, Set<Measurement> parents) {
        this(UUID.randomUUID().toString(), point, parents);
    }

    /**
     * Creates a Centroid with given vector coordinates and collected parents
     *
     * @param vector Coordinates
     * @param parents Measurements that were coalesced into this centroid
     */
    public Centroid(Double[] vector, Set<Measurement> parents) {
        this(UUID.randomUUID().toString(), vector, parents);
    }

    /**
     * Creates a Centroid with given UUID, vector coordinates and collected parents
     *
     * @param uuid Identifier of the centroid
     * @param vector Coordinates
     * @param parents Measurements that were coalesced into this centroid
     */
    public Centroid(String uuid, Double[] vector, Set<Measurement> parents) {
        this(uuid, new Point(vector), parents);
    }

    public Centroid(String uuid, Point point, Set<Measurement> parents) {
        super(uuid, point, parents);
    }

    /**
     * Recalculates the position of a centroid based on its current coalesced measurements.
     * Potentially useful for a plain implementation of the KMeans aggregator.
     *
     * @return new Centroid with updated position and the same measurements
     */
    public Centroid recalculatePosition() {
        Set<Measurement> measurements = getMeasurements();

        if (measurements.size() == 0) return new Centroid(this.getPoint(), new HashSet<>());
        double[] sum = null;

        for (Measurement measurement : measurements) {
            Point point = measurement.getPoint();

            if (sum == null) {
                sum = new double[point.getVector().length];
            }

            for (int i = 0; i < sum.length; i++) {
                sum[i] += point.getVector()[i];
            }
        }

        double[] avg = new double[sum.length];
        for (int i = 0; i < avg.length; i++) {
            avg[i] = sum[i] / measurements.size();
        }

        return new Centroid(ArrayUtils.toObject(avg), new HashSet<>(measurements));
    }

    /**
     * Coordinates of the centroid
     *
     * @deprecated use {@link #getPoint()} instead.
     * @return coordinates of the centroid
     */
    public Double[] getVector() {
        return getPoint().getVector();
    }

    /**
     * Coalesced measurements of the centroid
     *
     * @return coalesced measurements of the centroid
     */
    public Set<Measurement> getMeasurements() {
        return Sets.newHashSet(getParents());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
