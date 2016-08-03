package net.caspervg.aggr.aggregation.kmeans.seed;

import net.caspervg.aggr.core.bean.Measurement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FirstSeeding implements SeedingStrategy {
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int n) {
        return new HashSet<>(new ArrayList<>(measurements).subList(0, n - 1));
    }
}
