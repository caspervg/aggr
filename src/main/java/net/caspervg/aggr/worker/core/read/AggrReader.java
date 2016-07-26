package net.caspervg.aggr.worker.core.read;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.util.AggrContext;

import java.util.Optional;

public interface AggrReader {
    /**
     * Optionally reads a measurement with given identifier from the channel.
     * Will return {@link Optional#EMPTY} in case no measurement with given identifier was found.
     *
     * @param id Identifier of the measurement to read
     * @param context Context of the operation
     * @return Requested identifier
     */
    Optional<Measurement> read(String id, AggrContext context);

    /**
     * Reads all measurements from the channel.
     *
     * @param context Context of the operation
     * @return Measurements in the channel
     */
    Iterable<Measurement> read(AggrContext context);
}
