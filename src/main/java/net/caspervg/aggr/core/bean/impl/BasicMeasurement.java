package net.caspervg.aggr.core.bean.impl;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import org.apache.commons.lang3.ArrayUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BasicMeasurement implements Measurement {

    private double[] vector;
    public BasicMeasurement(double[] vector) {
        this.vector = vector;
    }

    @Override
    public Set<UniquelyIdentifiable> getParents() {
        return null;
    }

    @Override
    public void setParents(Set<UniquelyIdentifiable> parents) {

    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public void setUuid(String uuid) {

    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public Double[] getVector() {
        return ArrayUtils.toObject(vector);
    }

    @Override
    public void setVector(Double[] vector) {
        this.vector = ArrayUtils.toPrimitive(vector);
    }

    @Override
    public Optional<LocalDateTime> getTimestamp() {
        return null;
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {

    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public Optional<Object> getDatum(String key) {
        return null;
    }

    @Override
    public void setData(Map<String, Object> data) {

    }

    @Override
    public void setDatum(String key, Object value) {

    }

    @Override
    public List<String> getReadKeys() {
        return null;
    }

    @Override
    public List<String> getWriteKeys() {
        return null;
    }

    @Override
    public boolean canCombine(Measurement other) {
        return false;
    }

    @Override
    public int combinationHash() {
        return 0;
    }

    @Override
    public Measurement combine(Measurement other) {
        return null;
    }

    @Override
    public Measurement combine(Iterable<Measurement> others) {
        return null;
    }
}
