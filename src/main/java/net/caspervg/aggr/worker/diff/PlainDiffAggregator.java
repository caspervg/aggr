package net.caspervg.aggr.worker.diff;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;
import net.caspervg.aggr.worker.core.bean.impl.WeightedGeoMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;

import java.util.HashSet;
import java.util.Set;

public class PlainDiffAggregator extends AbstractDiffAggregator {

    public PlainDiffAggregator(Iterable<Measurement> subtrahends) {
        super(subtrahends);
    }

    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        String subtrahendFileName = context.getParameters().get(SUBTRAHEND_PARAM_KEY);

        Set<Measurement> results = new HashSet<>();

        Class<? extends Measurement> clazz = context.getOutputClass();

        // Find combinations
        for (Measurement measurement : measurements) {
            boolean foundPossibility = false;

            for (Measurement subtrahend : subtrahends) {
                if (subtrahend.canCombine(measurement)) {
                    Measurement difference = newInstance(clazz);
                    difference.setData(measurement.getData());
                    difference.setVector(measurement.getVector());

                    int minuendWt = Integer.valueOf(String.valueOf(measurement.getDatum(WeightedGeoMeasurement.WEIGHT_KEY).get()));
                    int subtrahendWt = Integer.valueOf(String.valueOf(subtrahend.getDatum(WeightedGeoMeasurement.WEIGHT_KEY).get()));
                    int diffWt = minuendWt - subtrahendWt;

                    difference.setDatum(WeightedGeoMeasurement.WEIGHT_KEY, diffWt);
                    difference.setParents(Sets.newHashSet(measurement, subtrahend));

                    results.add(difference);
                    foundPossibility = true;
                    break;
                }
            }

            if (! foundPossibility) {
                // If we haven't found a match, add the measurement plain and simple
                results.add(measurement);
            }
        }

        return Lists.newArrayList(
                new AggregationResult<>(
                        new DiffAggregation(
                                dataset,
                                subtrahendFileName,
                                Lists.newArrayList(measurements),
                                results
                        ),
                        results
                )
        );

    }
}
