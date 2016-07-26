package net.caspervg.aggr.worker.core.bean;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Measurement implements Serializable, UniquelyIdentifiable {

    private Point point;
    private String uuid;
    private Iterable<Measurement> parents;

    protected Measurement(String uuid, Point point, Iterable<Measurement> parents) {
        this.point = point;
        if (StringUtils.isBlank(uuid)) {
            this.uuid = UUID.randomUUID().toString();
        } else {
            this.uuid = uuid;
        }
        if (parents == null) {
            this.parents = new HashSet<>();
        } else {
            this.parents = parents;
        }
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public Iterable<Measurement> getParents() {
        return parents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measurement)) return false;

        Measurement that = (Measurement) o;

        if (point != null ? !point.equals(that.point) : that.point != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = point != null ? point.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "point=" + point +
                ", uuid='" + uuid + '\'' +
                ", parent='" + parents + '\'' +
                '}';
    }

    public static final class Builder {
        private Point point;
        private String uuid = UUID.randomUUID().toString();
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

        public Measurement build() {
            return new Measurement(uuid, point,parents);
        }
    }
}
