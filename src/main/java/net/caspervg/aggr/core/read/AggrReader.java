package net.caspervg.aggr.core.read;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.Optional;

public interface AggrReader {
    Optional<Measurement> read(String id, AggrContext context);
    Iterable<Measurement> read(AggrContext context);
}
