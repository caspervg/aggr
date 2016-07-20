package net.caspervg.aggr.core.bean;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class TimedMeasurement extends Measurement implements Serializable {

    private LocalDateTime timestamp;

    public TimedMeasurement(Point point, LocalDateTime timestamp) {
        this(UUID.randomUUID().toString(), point, timestamp);
    }

    public TimedMeasurement(String uuid, Point point, LocalDateTime timestamp) {
        this(uuid, point, Lists.newArrayList(), timestamp);
    }

    public TimedMeasurement(Point point, String parentId, LocalDateTime timestamp) {
        this(point, Lists.newArrayList(new Measurement(parentId)), timestamp);
    }

    public TimedMeasurement(Point point, Iterable<Measurement> parents, LocalDateTime timestamp) {
        this(UUID.randomUUID().toString(), point, parents, timestamp);
    }

    public TimedMeasurement(String uuid, Point point, String parentId, LocalDateTime timestamp) {
        this(uuid, point, Lists.newArrayList(new Measurement(parentId)), timestamp);
    }

    public TimedMeasurement(String uuid, Point point, Iterable<Measurement> parents, LocalDateTime timestamp) {
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
}
