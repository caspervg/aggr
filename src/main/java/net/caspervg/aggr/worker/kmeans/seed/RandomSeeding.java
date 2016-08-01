package net.caspervg.aggr.worker.kmeans.seed;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.*;

public class RandomSeeding implements SeedingStrategy {

    private static final SeedingStrategy FIRST_SEEDING = new FirstSeeding();

    /**
     * {@inheritDoc}
     *
     * Selects the seeds entirely randomly (without replacement).
     *
     * @param measurements {@inheritDoc}
     * @param n {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int n) {
        List<Measurement> measurementList = new ArrayList<>(measurements);
        Collections.shuffle(measurementList);
        return FIRST_SEEDING.seeds(measurements, n);
    }
}
