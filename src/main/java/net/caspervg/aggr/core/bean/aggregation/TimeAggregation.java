package net.caspervg.aggr.core.bean.aggregation;

import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public class TimeAggregation extends AbstractAggregation implements Serializable {

    private LocalDateTime start;
    private LocalDateTime end;

    public TimeAggregation(String uuid, Dataset dataset, LocalDateTime start, LocalDateTime end, Collection<Measurement> source) {
        super(uuid, dataset, source, AggregationType.TIME);
        this.start = start;
        this.end = end;
    }

    public TimeAggregation(Dataset dataset, LocalDateTime start, LocalDateTime end, Collection<Measurement> source) {
        this(UUID.randomUUID().toString(), dataset, start, end, source);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
