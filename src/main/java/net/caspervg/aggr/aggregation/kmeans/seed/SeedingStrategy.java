package net.caspervg.aggr.aggregation.kmeans.seed;

import net.caspervg.aggr.core.bean.Measurement;

import java.util.Collection;
import java.util.Set;


public interface SeedingStrategy {
    /**
     * Chooses {@code n} measurements to be the first iteration seeds for the k-Means algorithm.
     *
     * @param measurements Measurements to select from
     * @param n Number of seeds to select
     * @return Selected measurements
     */
    Set<Measurement> seeds(Collection<Measurement> measurements, int n);
}
