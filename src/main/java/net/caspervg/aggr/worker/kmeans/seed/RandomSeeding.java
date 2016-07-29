package net.caspervg.aggr.worker.kmeans.seed;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.*;

public class RandomSeeding implements SeedingStrategy {

    private static final SeedingStrategy FIRST_SEEDING = new FirstSeeding();
    @Override
    public Set<Measurement> seeds(Collection<Measurement> measurements, int numClusters) {
        List<Measurement> measurementList = new ArrayList<>(measurements);
        Collections.shuffle(measurementList);
        return FIRST_SEEDING.seeds(measurements, numClusters);
    }
}
