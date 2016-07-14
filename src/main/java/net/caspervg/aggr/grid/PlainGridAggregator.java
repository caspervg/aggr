package net.caspervg.aggr.grid;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.HashSet;
import java.util.Set;

public class PlainGridAggregator implements GridAggregator {

    private static final String DEFAULT_GRID_SIZE = "0.0005";

    @Override
    public Iterable<AggregationResult<GridAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        double gridSize = Double.parseDouble(
                context.getParameters().getOrDefault("grid_size", DEFAULT_GRID_SIZE)
        );

        Set<Measurement> roundedMeasurements = new HashSet<>();

        for (Measurement parent : measurements) {
            double latitude = parent.getPoint().getVector()[0];
            double longitude = parent.getPoint().getVector()[1];

            double roundedLatitude = (double) Math.round(latitude / gridSize) * gridSize;
            double roundedLongitude = (double) Math.round(longitude / gridSize) * gridSize;

            Point roundedPoint = new Point(new Double[]{roundedLatitude, roundedLongitude});
            roundedMeasurements.add(new Measurement(roundedPoint, parent.getUuid(), parent.getTimestamp()));
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
