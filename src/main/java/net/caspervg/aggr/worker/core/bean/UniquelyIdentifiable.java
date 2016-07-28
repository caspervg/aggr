package net.caspervg.aggr.worker.core.bean;

import java.io.Serializable;

public interface UniquelyIdentifiable extends Serializable {
    String getUuid();
    void setUuid(String uuid);
    String getUri();
}
