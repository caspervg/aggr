package net.caspervg.aggr.core.bean;

import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Measurement implements Serializable, UniquelyIdentifiable {

    private Point point;
    private String uuid;
    private Iterable<Measurement> parents;

    public Measurement(String uuid) {
        this.uuid = uuid;
        this.point = new Point(new Double[]{-1.0, -1.0});
        this.parents = new ArrayList<>();
    }

    public Measurement(Point point) {
        this(UUID.randomUUID().toString(), point);
    }

    public Measurement(String uuid, Point point) {
        this(uuid, point, null);
    }

    public Measurement(Point point, String parentId) {
        this(point, Lists.newArrayList(new Measurement(parentId)));
    }

    public Measurement(Point point, Iterable<Measurement> parents) {
        this(UUID.randomUUID().toString(), point, parents);
    }

    public Measurement(String uuid, Point point, Iterable<Measurement> parents) {
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
}
