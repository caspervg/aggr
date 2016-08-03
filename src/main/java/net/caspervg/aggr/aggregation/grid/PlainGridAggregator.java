package net.caspervg.aggr.aggregation.grid;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlainGridAggregator extends AbstractGridAggregator {


    @Override
    public Iterable<AggregationResult<GridAggregation, Measurement>> aggregate(Dataset dataset,
                                                                                       Iterable<Measurement> measurements,
                                                                                       AggrContext context) {
        double gridSize = Double.parseDouble(
                context.getParameters().getOrDefault(GRID_SIZE_PARAM, DEFAULT_GRID_SIZE)
        );

        List<Measurement> measurementList = Lists.newArrayList(measurements);
        Set<Measurement> roundedMeasurements = new HashSet<>();

        for (Measurement parent : measurements) {
            Double[] parentVec = parent.getVector();
            Double[] roundedVec = new Double[parentVec.length];

            for (int i = 0; i < parentVec.length; i++) {
                roundedVec[i] = (double) Math.round(parentVec[i] / gridSize) * gridSize;
            }

            Measurement child = context.newOutputMeasurement();
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
                                measurementList,
                                roundedMeasurements
                        ),
                        roundedMeasurements
                )
        );
    }
}
