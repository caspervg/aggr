package net.caspervg.aggr.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Measurement extends UniquelyIdentifiable, Combinable, Child, Serializable {
    /**
     * Retrieves the vector for this measurement.
     *
     * @return Vector for this measurement.
     */
    Double[] getVector();

    /**
     * Sets the vector for this measurement.
     *
     * @param vector Vector to set
     */
    void setVector(Double[] vector);

    /**
     * Retrieves the timestamp for this measurement. Implementations may return {@link Optional#empty()} if
     * they do not support timestamps.
     *
     * @return Timestamp for this measurement, if it exists.
     */
    Optional<LocalDateTime> getTimestamp();

    /**
     * Sets the timestamp for this measurement. Implementations may ignore this call if they
     * do not support timestamps.
     *
     * @param timestamp Timestamp to set.
     */
    void setTimestamp(LocalDateTime timestamp);

    /**
     * Retrieves the data that is stored in this measurement.
     *
     * This map should at least contain a value for all keys present in {@link #getWriteKeys()}.
     *
     * @return Data stored in this measurement
     */
    Map<String, Object> getData();

    /**
     * Retrieves a single piece of information with given key from this measurement.
     *
     * @param key Key of the information to retrieve.
     * @return Requested piece of information, if it exists in this measurement.
     */
    Optional<Object> getDatum(String key);

    /**
     * Sets the data in the given map in this measurement.
     *
     * @param data Data to set.
     */
    void setData(Map<String, Object> data);

    /**
     * Sets a single piece of information with given key and value in this measurement.
     *
     * Implementations should at least accept all keys present in {@link #getReadKeys()}
     *
     * @param key Key of the information to set.
     * @param value Value of the information to set.
     * @throws IllegalArgumentException if the callee does not accept given key.
     */
    void setDatum(String key, Object value);

    /**
     * Returns a list of keys that are used to deserialize this measurement from an input channel. These keys
     * should be used in the {@link #setData(Map)} call.
     *
     * It is suggested, but not required, that this returns the same keys as {@link #getWriteKeys()}.
     *
     * @return List of keys for deserialization.
     */
    List<String> getReadKeys();

    /**
     * Returns a list of keys that are to be used to serialize this measurement to an output channel. These
     * keys should indicate values in the return value of {@link #getData()}.
     *
     * It is suggested, but not required, that this returns the same keys as {@link #getWriteKeys()}.
     *
     * @return List of keys for serialization
     */
    List<String> getWriteKeys();
}
