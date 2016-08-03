package net.caspervg.aggr.aggregation.kmeans.seed;

import net.caspervg.aggr.core.bean.Measurement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FirstSeeding implements SeedingStrategy {
    /**
     * Selects the first {@code n} measurements as seeds.
     *
     * @param measurements {@inheritDoc}
     * @param n {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int n) {
        return new HashSet<>(new ArrayList<>(measurements).subList(0, n - 1));
    }
}
