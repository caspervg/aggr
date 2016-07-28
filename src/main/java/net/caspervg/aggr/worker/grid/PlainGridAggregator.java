package net.caspervg.aggr.worker.grid;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.core.bean.*;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;

import java.util.HashSet;
import java.util.Set;

public class PlainGridAggregator extends AbstractGridAggregator {


    @Override
    public Iterable<AggregationResult<GridAggregation, Measurement>> aggregate(Dataset dataset,
                                                                                       Iterable<Measurement> measurements,
                                                                                       AggrContext context) {
        double gridSize = Double.parseDouble(
                context.getParameters().getOrDefault(GRID_SIZE_PARAM, DEFAULT_GRID_SIZE)
        );

        Set<Measurement> roundedMeasurements = new HashSet<>();

        for (Measurement parent : measurements) {
            Double[] parentVec = parent.getVector();
            Double[] roundedVec = new Double[parentVec.length];

            for (int i = 0; i < parentVec.length; i++) {
                roundedVec[i] = (double) Math.round(parentVec[i] / gridSize) * gridSize;
            }

            Measurement child = context.newMeasurement();
            Set<UniquelyIdentifiable> parents = new HashSet<>();
            parents.add(parent);

            child.setParents(parents);
            child.setData(parent.getData());
            child.setVector(roundedVec);

            roundedMeasurements.add(child);
        }

        return Lists.newArrayList(
                new AggregationResult<>(
                        new GridAggregation(dataset,
                                gridSize,
                                roundedMeasurements
                        ),
                        roundedMeasurements
                )
        );
    }
}
