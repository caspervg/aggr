package net.caspervg.aggr.aggregation;

import java.io.IOException;
import java.net.URISyntaxException;

@FunctionalInterface
public interface AggregationExecution {
    /**
     * Sets-up the environment of an aggregation and then executes it.
     *
     * @throws URISyntaxException if the HDFS url is not valid
     * @throws IOException if certain files could not be read
     */
    void execute() throws URISyntaxException, IOException;
}
