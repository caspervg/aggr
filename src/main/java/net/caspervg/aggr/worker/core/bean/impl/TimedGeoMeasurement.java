package net.caspervg.aggr.worker.core.bean.impl;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setData(Map<String, Object> data) {
        super.setData(data);
        Object timeObj = data.get(TIME_KEY);
        if (timeObj != null) {
            String timeStr = String.valueOf(timeObj);
            if (StringUtils.isNotBlank(timeStr)) {
                this.timestamp = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME);
            }
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

}
