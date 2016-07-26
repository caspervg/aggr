package net.caspervg.aggr.worker.core;

import java.io.IOException;
import java.net.URISyntaxException;

@FunctionalInterface
public interface AggregationExecution {
    void execute() throws URISyntaxException, IOException;
}
