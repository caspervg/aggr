package net.caspervg.aggr.core.bean;

import java.io.Serializable;

public interface UniquelyIdentifiable extends Serializable {
    String getUuid();
    void setUuid(String uuid);
    String getUri();
}
