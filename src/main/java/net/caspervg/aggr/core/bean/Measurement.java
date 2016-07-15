package net.caspervg.aggr.core.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class Measurement implements Serializable {

    private Point point;
    private String uuid;
    private String parent;
    private LocalDateTime timestamp;

    public Measurement(Point point, LocalDateTime timestamp) {
        this(UUID.randomUUID().toString(), point, timestamp);
    }

    public Measurement(String uuid, Point point, LocalDateTime timestamp) {
        this(uuid, point, null, timestamp);
    }

    public Measurement(Point point, String parent, LocalDateTime timestamp) {
        this(UUID.randomUUID().toString(), point, parent, timestamp);
    }

    public Measurement(String uuid, Point point, String parent, LocalDateTime timestamp) {
        this.point = point;
        if (StringUtils.isBlank(uuid)) {
            this.uuid = UUID.randomUUID().toString();
        } else {
            this.uuid = uuid;
        }
        if (StringUtils.isBlank(parent)) {
            this.parent = null;
        } else {
            this.parent = parent;
        }
        this.timestamp = timestamp;
    }

    public Point getPoint() {
        return point;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Optional<String> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measurement)) return false;

        Measurement that = (Measurement) o;

        if (point != null ? !point.equals(that.point) : that.point != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = point != null ? point.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "point=" + point +
                ", uuid='" + uuid + '\'' +
                ", parent='" + parent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
