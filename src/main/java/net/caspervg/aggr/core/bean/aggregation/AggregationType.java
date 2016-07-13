package net.caspervg.aggr.core.bean.aggregation;

import java.io.Serializable;

public enum AggregationType implements Serializable {
    TIME("time"),
    GRID("grid"),
    KMEANS("kmeans");

    private String serialization;

    AggregationType(String serialization) {
        this.serialization = serialization;
    }

    public String getSerialization() {
        return serialization;
    }
}
