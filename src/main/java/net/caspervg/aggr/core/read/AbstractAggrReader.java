package net.caspervg.aggr.core.read;

import java.util.Map;

public abstract class AbstractAggrReader implements AggrReader {

    protected static final String DEFAULT_LAT_KEY = "latitude";
    protected static final String DEFAULT_LON_KEY = "longitude";
    protected static final String DEFAULT_TIMESTAMP_KEY = "timestamp";
    protected static final String DEFAULT_ID_KEY = "id";
    protected static final String DEFAULT_SOURCE_KEY = "source";

    protected String latitudeKey(Map<String, String> parameters) {
        return parameters.getOrDefault("latitude_key", DEFAULT_LAT_KEY);
    }

    protected String longitudeKey(Map<String, String> parameters) {
        return parameters.getOrDefault("longitude_key", DEFAULT_LON_KEY);
    }

    protected String timestampKey(Map<String, String> parameters) {
        return parameters.getOrDefault("timestamp_key", DEFAULT_TIMESTAMP_KEY);
    }

    protected String idKey(Map<String, String> parameters) {
        return parameters.getOrDefault("id_key", DEFAULT_ID_KEY);
    }

    protected String sourceKey(Map<String, String> parameters) {
        return parameters.getOrDefault("source_key", DEFAULT_SOURCE_KEY);
    }
}
