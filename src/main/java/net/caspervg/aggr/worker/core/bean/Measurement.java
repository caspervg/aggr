package net.caspervg.aggr.worker.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Measurement extends UniquelyIdentifiable, Parental, Serializable {
    Double[] getVector();
    void setVector(Double[] vector);

    LocalDateTime getTimestamp();
    void setTimestamp(LocalDateTime timestamp);

    Map<String, Object> getData();
    Object getDatum(String key);
    void setData(Map<String, Object> data);
    void setDatum(String key, Object datum);

    List<String> getReadKeys();
    List<String> getWriteKeys();
}
