package net.caspervg.aggr.worker.grid;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.Point;
import net.caspervg.aggr.worker.core.bean.TimedMeasurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SparkGridAggregator extends AbstractGridAggregator implements Serializable {

    @Override
    public Iterable<AggregationResult<GridAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        double gridSize = Double.parseDouble(
                context.getParameters().getOrDefault(GRID_SIZE_PARAM, DEFAULT_GRID_SIZE)
        );

        JavaRDD<Measurement> measRDD = context.getSparkContext().parallelize(Lists.newArrayList(measurements));

        // Map each measurement so that it sits on top of the grid (rounding)
        JavaRDD<Measurement> roundedMeasRDD = measRDD.map((Function<Measurement, Measurement>) parent -> {
            double latitude = parent.getPoint().getVector()[0];
            double longitude = parent.getPoint().getVector()[1];

            double roundedLatitude = (double) Math.round(latitude / gridSize) * gridSize;
            double roundedLongitude = (double) Math.round(longitude / gridSize) * gridSize;

            Point roundedPoint = new Point(new Double[]{roundedLatitude, roundedLongitude});

            if (parent instanceof TimedMeasurement) {
                return TimedMeasurement.Builder
                        .setup()
                        .withPoint(roundedPoint)
                        .withParent(parent)
                        .withTimestamp(((TimedMeasurement) parent).getTimestamp())
                        .build();
            } else {
                return Measurement.Builder
                        .setup()
                        .withPoint(roundedPoint)
                        .withParent(parent)
                        .build();
            }
        });

        List<Measurement> childMeasurements = roundedMeasRDD.collect();

        // Return the result of the aggregation
        return Lists.newArrayList(
                new AggregationResult<>(
                        new GridAggregation(dataset,
                                gridSize,
                                Lists.newArrayList(childMeasurements)
                        ),
                        childMeasurements
                )
        );
    }
}