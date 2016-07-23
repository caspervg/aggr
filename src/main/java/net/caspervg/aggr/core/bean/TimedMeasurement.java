package net.caspervg.aggr.core.bean;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TimedMeasurement extends Measurement implements Serializable {

    private LocalDateTime timestamp;

    protected TimedMeasurement(String uuid,
                            Point point,
                            Iterable<Measurement> parents,
                            LocalDateTime timestamp) {
        super(uuid, point, parents);
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimedMeasurement)) return false;
        if (!super.equals(o)) return false;

        TimedMeasurement that = (TimedMeasurement) o;

        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TimedMeasurement{" +
                "timestamp=" + timestamp +
                '}';
    }


    public static final class Builder {
        private String uuid = UUID.randomUUID().toString();
        private LocalDateTime timestamp;
        private Point point;
        private Set<Measurement> parents = new HashSet<>();

        private Builder() {
        }

        public static Builder setup() {
            return new Builder();
        }

        public Builder withUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withPoint(Point point) {
            this.point = point;
            return this;
        }

        public Builder withParents(Iterable<Measurement> parents) {
            this.parents = Sets.newHashSet(parents);
            return this;
        }

        public Builder withParent(String parentId) {
            return withParent(Measurement.Builder.setup().withUuid(parentId).build());
        }

        public Builder withParent(Measurement parent) {
            this.parents.add(parent);
            return this;
        }

        public TimedMeasurement build() {
            return new TimedMeasurement(uuid, point, parents, timestamp);
        }
    }
}
