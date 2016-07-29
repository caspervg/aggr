package net.caspervg.aggr.worker.kmeans.seed;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FirstSeeding implements SeedingStrategy {
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int numClusters) {
        return new HashSet<>(new ArrayList<>(measurements).subList(0, numClusters - 1));
    }
}
