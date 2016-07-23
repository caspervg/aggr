package net.caspervg.aggr.core.bean;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Result of a KMeans aggregation
 */
public class Centroid extends Measurement implements Serializable, UniquelyIdentifiable {

    protected Centroid(String uuid, Point point, Set<Measurement> parents) {
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

        if (measurements.size() == 0) return Builder.setup().withPoint(this.getPoint()).build();
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

        return Builder
                .setup()
                .withPoint(new Point(ArrayUtils.toObject(avg)))
                .withParents(measurements)
                .build();
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

    public static final class Builder {
        private String uuid = UUID.randomUUID().toString();
        private Point point;
        private Set<Measurement> parents = new HashSet<>();

        private Builder() {
        }

        public static Builder setup() {
            return new Builder();
        }

        public Builder withPoint(Point point) {
            this.point = point;
            return this;
        }

        public Builder withUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withParents(Collection<Measurement> parents) {
            this.parents = new HashSet<>(parents);
            return this;
        }

        public Centroid build() {
            return new Centroid(uuid, point, parents);
        }
    }
}
