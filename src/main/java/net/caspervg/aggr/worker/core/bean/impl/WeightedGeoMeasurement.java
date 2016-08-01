package net.caspervg.aggr.worker.core.bean.impl;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.UniquelyIdentifiable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class WeightedGeoMeasurement extends GeoMeasurement {

    public static final String WEIGHT_KEY = "weight";

    private long weight;

    @Override
    public void setData(Map<String, Object> data) {
        super.setData(data);
        Object weightObj = data.get(WEIGHT_KEY);
        if (weightObj != null) {
            String weightStr = String.valueOf(weightObj);
            if (StringUtils.isNotBlank(weightStr)) {
                this.weight = Long.parseLong(weightStr);
            }
        }

    }

    @Override
    public void setDatum(String key, Object value) {
        if (key.equalsIgnoreCase(WEIGHT_KEY)) {
            this.weight = Long.parseLong(String.valueOf(value));
        } else {
            super.setDatum(key, value);
        }
    }

    @Override
    public Optional<Object> getDatum(String key) {
        if (key.equalsIgnoreCase(WEIGHT_KEY)) {
            return Optional.of(this.weight);
        } else {
            return super.getDatum(key);
        }
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = super.getData();
        data.put(WEIGHT_KEY, weight);

        return data;
    }

    @Override
    public List<String> getReadKeys() {
        List<String> keys = super.getReadKeys();
        keys.add(WEIGHT_KEY);
        return keys;
    }

    @Override
    public List<String> getWriteKeys() {
        List<String> keys = super.getWriteKeys();
        keys.add(WEIGHT_KEY);
        return keys;
    }

    @Override
    public boolean canCombine(Measurement other) {
        return other.getDatum(WEIGHT_KEY).isPresent() && super.canCombine(other);
    }

    @Override
    public Measurement combine(Measurement other) {
        if (! canCombine(other)) {
            throw new IllegalArgumentException("Other measurement must have the same vector");
        }

        Optional<Object> otherWeightObj = other.getDatum(WEIGHT_KEY);
        if (! (otherWeightObj.isPresent())) {
            throw new IllegalArgumentException("Other measurement must be weighted");
        } else {
            Measurement combined = new WeightedGeoMeasurement();

            combined.setData(this.getData());
            combined.setVector(this.getVector());

            combined.setDatum(WEIGHT_KEY, this.weight + (long) otherWeightObj.get());
            Set<UniquelyIdentifiable> parents = new HashSet<>();
            parents.add(this);
            parents.add(other);
            combined.setParents(parents);

            return combined;
        }
    }

    public Measurement combine(Iterable<Measurement> others) {
        Measurement combined = new WeightedGeoMeasurement();

        combined.setData(this.getData());
        combined.setVector(this.getVector());

        long weightSum = this.weight;

        Set<UniquelyIdentifiable> parents = new HashSet<>();
        parents.add(this);
        for (Measurement other : others) {
            if (!canCombine(other)) {
                throw new IllegalArgumentException("Other measurement must have the same vector and be weighted");
            }
            Optional<Object> otherWeightObj = other.getDatum(WEIGHT_KEY);
            if (!(otherWeightObj.isPresent())) {
                throw new IllegalArgumentException("Other measurement must be weighted");
            } else {
                weightSum += Long.parseLong(String.valueOf(otherWeightObj.get()));
                parents.add(other);
            }
        }

        combined.setDatum(WEIGHT_KEY, weightSum);
        combined.setParents(parents);

        return combined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeightedGeoMeasurement)) return false;
        if (!super.equals(o)) return false;

        WeightedGeoMeasurement that = (WeightedGeoMeasurement) o;

        return weight == that.weight;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (weight ^ (weight >>> 32));
        return result;
    }
}
