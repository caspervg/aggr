package net.caspervg.aggr.worker.core.bean.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

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
    public void setDatum(String key, Object datum) {
        if (key.equalsIgnoreCase(WEIGHT_KEY)) {
            this.weight = Long.parseLong(String.valueOf(datum));
        }
    }

    @Override
    public Object getDatum(String key) {
        if (key.equalsIgnoreCase(WEIGHT_KEY)) {
            return this.weight;
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

}
