package net.caspervg.aggr.aggregation.basic.combination;

import com.google.common.collect.Lists;
import net.caspervg.aggr.aggregation.basic.BasicAggregator;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.aggregation.basic.BasicAggregation;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.*;

public class PlainCombinationAggregator implements BasicAggregator {
    @Override
    public Iterable<AggregationResult<BasicAggregation, Measurement>> aggregate(Dataset dataset,
                                                                                Iterable<Measurement> measurements,
                                                                                AggrContext context) {
        Map<Measurement, List<Measurement>> combinations = new HashMap<>();

        // Find combinations
        for (Measurement measurement : measurements) {
            boolean foundPossibility = false;

            for (Measurement possibleCombo : combinations.keySet()) {
                if (possibleCombo.canCombine(measurement)) {
                    combinations.get(possibleCombo).add(measurement);
                    foundPossibility = true;
                    break;
                }
            }

            if (! foundPossibility) {
                combinations.put(measurement, Lists.newArrayList(measurement));
            }
        }

        Set<Measurement> result = new HashSet<>();
        // Create combinations
        for (List<Measurement> toCombine : combinations.values()) {
            Measurement first = toCombine.get(0);
            result.add(first.combine(toCombine.subList(1, toCombine.size())));
        }

        return Lists.newArrayList(
                new AggregationResult<>(
                        new BasicAggregation(
                                dataset,
                                Lists.newArrayList(measurements),
                                result
                        ),
                        result
                )
        );
    }

}
