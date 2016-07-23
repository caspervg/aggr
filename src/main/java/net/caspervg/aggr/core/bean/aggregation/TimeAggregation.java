package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Aggregation that buckets the source data by time
 */
public class TimeAggregation extends AbstractAggregation implements Serializable {

    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Creates a TimeAggregation with given UUID, start- and end times.
     *
     * @param uuid Identifier of the aggregation
     * @param dataset Dataset that was used
     * @param start Start time
     * @param end End time
     * @param source Source data
     */
    public TimeAggregation(String uuid, Dataset dataset, LocalDateTime start, LocalDateTime end, Collection<Measurement> source) {
        super(uuid, dataset, source);
        this.start = start;
        this.end = end;
    }

    /**
     * Creates a TimeAggregation with given start- and end times.
     *
     * @param dataset Dataset that was used
     * @param start Start time
     * @param end End time
     * @param source Source data
     */
    public TimeAggregation(Dataset dataset, LocalDateTime start, LocalDateTime end, Collection<Measurement> source) {
        this(UUID.randomUUID().toString(), dataset, start, end, source);
    }

    /**
     * Retrieves the start time of this time aggregation bucket
     *
     * @return Start time
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Retrieves the end time of this time aggregation bucket
     *
     * @return End time
     */
    public LocalDateTime getEnd() {
        return end;
    }
}
