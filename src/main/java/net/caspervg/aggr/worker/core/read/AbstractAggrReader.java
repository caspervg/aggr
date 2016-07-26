package net.caspervg.aggr.worker.core.read;

import net.caspervg.aggr.worker.core.util.Constants;

import java.util.Map;

public abstract class AbstractAggrReader implements AggrReader {

    public static final String INPUT_PARAM_KEY = "input";
    protected static final String DEFAULT_LAT_KEY = Constants.DEFAULT_LAT_KEY;
    protected static final String DEFAULT_LON_KEY = Constants.DEFAULT_LON_KEY;
    protected static final String DEFAULT_TIMESTAMP_KEY = Constants.DEFAULT_TIMESTAMP_KEY;
    protected static final String DEFAULT_ID_KEY = Constants.DEFAULT_ID_KEY;
    protected static final String DEFAULT_SOURCE_KEY = Constants.DEFAULT_SOURCE_KEY;

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
