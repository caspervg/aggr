package net.caspervg.aggr.core.bean;

import java.io.Serializable;

public interface UniquelyIdentifiable extends Serializable {
    /**
     * Returns the universal unique identifier of this entity.
     *
     * @return UUID of the entity
     * @see java.util.UUID
     */
    String getUuid();

    /**
     * Sets the universal unique identifier of this entity.
     *
     * @param uuid UUID to set
     * @see java.util.UUID
     */
    void setUuid(String uuid);

    /**
     * Retrieves the URI, as a String, of this entity
     *
     * @return URI of this entity
     */
    String getUri();
}
