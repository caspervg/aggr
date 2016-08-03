package net.caspervg.aggr.aggregation.average;

import net.caspervg.aggr.aggregation.AbstractAggregation;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;

import java.util.Collection;
import java.util.UUID;

public class AverageAggregation extends AbstractAggregation {

    private String others;
    private long amount;
    private String key;

    public AverageAggregation(Dataset dataset, String others, long amount, String key, Collection<Measurement> sources, Collection<Measurement> results) {
        this(UUID.randomUUID().toString(), dataset, others, amount, key, sources, results);
    }

    public AverageAggregation(String uuid, Dataset dataset, String others, long amount, String key, Collection<Measurement> sources, Collection<Measurement> results) {
        super(uuid, dataset, sources, results);
        this.others = others;
        this.amount = amount;
        this.key = key;
    }

    public String getOthers() {
        return others;
    }

    public long getAmount() {
        return amount;
    }

    public String getKey() {
        return key;
    }
}
