package net.caspervg.aggr.ext;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimedGeoMeasurement extends GeoMeasurement {

    public static final String TIME_KEY = "timestamp";

    private LocalDateTime timestamp;

    public TimedGeoMeasurement() {
        super();
    }

    public TimedGeoMeasurement(String uuid) {
        super(uuid);
    }

    @Override
    public Optional<LocalDateTime> getTimestamp() {
        return Optional.ofNullable(this.timestamp);
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setData(Map<String, Object> data) {
        super.setData(data);

        LocalDateTime possibTs = timestampFromObj(data.get(TIME_KEY));
        if (possibTs != null) {
            this.timestamp = possibTs;
        }
    }

    @Override
    public void setDatum(String key, Object value) {
        switch (key) {
            case TIME_KEY:
                this.timestamp = timestampFromObj(value);
                break;
            default:
                super.setDatum(key, value);
        }
    }

    @Override
    public Optional<Object> getDatum(String key) {
        switch (key) {
            case TIME_KEY:
                return Optional.of(this.timestamp);
            default:
                return super.getDatum(key);
        }
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = super.getData();
        data.put(TIME_KEY, timestamp);

        return data;
    }

    @Override
    public List<String> getReadKeys() {
        List<String> keys = super.getReadKeys();
        keys.add(TIME_KEY);
        return keys;
    }

    @Override
    public List<String> getWriteKeys() {
        List<String> keys = super.getWriteKeys();
        keys.add(TIME_KEY);
        return keys;
    }

    @Override
    public boolean canCombine(Measurement other) {
        return Optional.ofNullable(this.timestamp).equals(other.getTimestamp()) && super.canCombine(other);
    }

    @Override
    public int combinationHash() {
        return 31 * super.combinationHash() + (timestamp != null ? timestamp.hashCode() : 0);
    }

    @Override
    public Measurement combine(Measurement other) {
        if (! canCombine(other)) {
            throw new IllegalArgumentException("Other measurement must have the same vector and timestamp");
        }

        Measurement combined = new TimedGeoMeasurement();

        combined.setData(this.getData());

        combined.setVector(this.getVector());

        Set<UniquelyIdentifiable> parents = new HashSet<>();
        parents.add(this);
        parents.add(other);
        combined.setParents(parents);

        return combined;
    }

    @Override
    public Measurement combine(Iterable<Measurement> others) {
        Measurement combined = new TimedGeoMeasurement();

        combined.setData(this.getData());
        combined.setVector(this.getVector());

        Set<UniquelyIdentifiable> parents = new HashSet<>();
        parents.add(this);
        for (Measurement other : others) {
            if (! canCombine(other)) {
                throw new IllegalArgumentException("Other measurement must have the same vector and timestamp");
            }
            parents.add(other);
        }
        combined.setParents(parents);

        return combined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimedGeoMeasurement)) return false;
        if (!super.equals(o)) return false;

        TimedGeoMeasurement that = (TimedGeoMeasurement) o;

        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    private LocalDateTime timestampFromObj(Object timeObj) {
        if (timeObj != null) {
            String timeStr = String.valueOf(timeObj);
            if (StringUtils.isNotBlank(timeStr)) {
                return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME);
            }
        }

        return null;
    }
}
