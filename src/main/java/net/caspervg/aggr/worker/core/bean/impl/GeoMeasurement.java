package net.caspervg.aggr.worker.core.bean.impl;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.worker.core.util.Constants;

import java.time.LocalDateTime;
import java.util.*;

public class GeoMeasurement implements Measurement {

    public static final String LAT_KEY = "latitude";
    public static final String LON_KEY = "longitude";
    protected static final String MEASUREMENT_URI_PREFIX = Constants.OWN_PREFIX + "measurements/";

    private String uuid;
    private Double[] vector;
    private Set<UniquelyIdentifiable> parents;

    public GeoMeasurement() {
        this(UUID.randomUUID().toString());
    }

    public GeoMeasurement(String uuid) {
        this.uuid = uuid;
        this.vector = new Double[]{};
        this.parents = new HashSet<>();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUri() {
        return MEASUREMENT_URI_PREFIX + getUuid();
    }

    @Override
    public Set<UniquelyIdentifiable> getParents() {
        return parents;
    }

    @Override
    public void setParents(Set<UniquelyIdentifiable> parents) {
        this.parents = parents;
    }

    @Override
    public Double[] getVector() {
        return this.vector;
    }

    @Override
    public void setVector(Double[] vector) {
        this.vector = vector;
    }

    @Override
    public Optional<LocalDateTime> getTimestamp() {
        System.err.println("GeoMeasurement does not support setting the timestamp. " +
                "Use TimedGeoMeasurement instead");
        return Optional.empty();
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        System.err.println("GeoMeasurement does not support setting the timestamp. " +
                "Use TimedGeoMeasurement instead");
    }

    @Override
    public void setData(Map<String, Object> data) {
        double lat = Double.valueOf(String.valueOf(data.getOrDefault(LAT_KEY, 0.0)));
        double lon = Double.valueOf(String.valueOf(data.getOrDefault(LON_KEY, 0.0)));

        this.vector = new Double[] {lat, lon};
    }

    @Override
    public void setDatum(String key, Object value) {
        switch (key) {
            case LAT_KEY:
                this.vector[0] = Double.valueOf(String.valueOf(value));
                break;
            case LON_KEY:
                this.vector[1] = Double.valueOf(String.valueOf(value));
                break;
            default:
                throw new IllegalArgumentException("Unsupported key in #setDatum(String, Object)");
        }
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put(LAT_KEY, vector[0]);
        data.put(LON_KEY, vector[1]);

        return data;
    }

    @Override
    public Optional<Object> getDatum(String key) {
        switch (key) {
            case LAT_KEY:
                return Optional.of(vector[0]);
            case LON_KEY:
                return Optional.of(vector[1]);
            default:
                return Optional.empty();
        }
    }

    @Override
    public List<String> getReadKeys() {
        return Lists.newArrayList(LAT_KEY, LON_KEY);
    }

    @Override
    public List<String> getWriteKeys() {
        return Lists.newArrayList(LAT_KEY, LON_KEY);
    }

    @Override
    public boolean canCombine(Measurement other) {
        return Arrays.deepEquals(this.getVector(), other.getVector());
    }

    @Override
    public Measurement combine(Measurement other) {
        if (! canCombine(other)) {
            throw new IllegalArgumentException("Other measurement must have the same vector");
        }

        Measurement combined = new GeoMeasurement();

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
        Measurement combined = new GeoMeasurement();
        combined.setData(this.getData());
        combined.setVector(this.getVector());

        Set<UniquelyIdentifiable> parents = new HashSet<>();
        parents.add(this);
        for (Measurement other : others) {
            if (! canCombine(other)) {
                throw new IllegalArgumentException("Other measurement must have the same vector");
            }
            parents.add(other);
        }
        combined.setParents(parents);

        return combined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoMeasurement)) return false;

        GeoMeasurement that = (GeoMeasurement) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (!Arrays.deepEquals(vector, that.vector)) return false;
        return parents != null ? parents.equals(that.parents) : that.parents == null;

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(vector);
        result = 31 * result + (parents != null ? parents.hashCode() : 0);
        return result;
    }
}
