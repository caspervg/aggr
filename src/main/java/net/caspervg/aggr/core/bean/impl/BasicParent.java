package net.caspervg.aggr.core.bean.impl;

import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.core.util.Constants;

public class BasicParent implements UniquelyIdentifiable {
    protected static final String MEASUREMENT_URI_PREFIX = Constants.OWN_PREFIX + "measurements/";

    private String uuid;

    public BasicParent(String uuid) {
        this.uuid = uuid;
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
}
