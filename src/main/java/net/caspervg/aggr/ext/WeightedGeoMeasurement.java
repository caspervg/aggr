package net.caspervg.aggr.ext;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class WeightedGeoMeasurement extends GeoMeasurement {

    public static final String WEIGHT_KEY = "weight";

    private double weight;

    @Override
    public void setData(Map<String, Object> data) {
        super.setData(data);
        Object weightObj = data.get(WEIGHT_KEY);
        if (weightObj != null) {
            String weightStr = String.valueOf(weightObj);
            if (StringUtils.isNotBlank(weightStr)) {
                this.weight = Double.parseDouble(weightStr);
            }
        }

    }

    @Override
    public void setDatum(String key, Object value) {
        if (key.equalsIgnoreCase(WEIGHT_KEY)) {
            this.weight = Double.parseDouble(String.valueOf(value));
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
    public int combinationHash() {
        return 31 * super.combinationHash();
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

            combined.setDatum(WEIGHT_KEY, this.weight + (double) otherWeightObj.get());
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

        double weightSum = this.weight;

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
        long temp;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
