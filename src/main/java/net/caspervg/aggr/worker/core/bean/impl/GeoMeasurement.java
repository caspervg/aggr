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
    public LocalDateTime getTimestamp() {
        System.err.println("GeoMeasurement does not support setting the timestamp. " +
                "Use TimedGeoMeasurement instead");
        return null;
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        System.err.println("GeoMeasurement does not support setting the timestamp. " +
                "Use TimedGeoMeasurement instead");
        return;
    }

    @Override
    public void setData(Map<String, Object> data) {
        double lat = Double.valueOf(String.valueOf(data.getOrDefault(LAT_KEY, 0.0)));
        double lon = Double.valueOf(String.valueOf(data.getOrDefault(LON_KEY, 0.0)));

        this.vector = new Double[] {lat, lon};
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put(LAT_KEY, vector[0]);
        data.put(LON_KEY, vector[1]);

        return data;
    }

    @Override
    public List<String> getReadKeys() {
        return Lists.newArrayList(LAT_KEY, LON_KEY);
    }

    @Override
    public List<String> getWriteKeys() {
        return Lists.newArrayList(LAT_KEY, LON_KEY);
    }
}
