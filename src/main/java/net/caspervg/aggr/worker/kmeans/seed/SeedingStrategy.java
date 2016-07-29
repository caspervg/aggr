package net.caspervg.aggr.worker.kmeans.seed;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.Collection;
import java.util.Set;

public interface SeedingStrategy {
    Set<Measurement> seeds(Collection<Measurement> measurements, int numClusters);
}
