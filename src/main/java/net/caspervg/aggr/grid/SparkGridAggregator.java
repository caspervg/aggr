package net.caspervg.aggr.grid;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.util.AggrContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SparkGridAggregator implements GridAggregator, Serializable {

    private static final String DEFAULT_GRID_SIZE = "0.0005";

    @Override
    public Iterable<AggregationResult<GridAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        double gridSize = Double.parseDouble(
                context.getParameters().getOrDefault("grid_size", DEFAULT_GRID_SIZE)
        );

        JavaRDD<Measurement> measRDD = context.getSparkContext().parallelize(Lists.newArrayList(measurements));

        JavaRDD<Measurement> roundedMeasRDD = measRDD.map((Function<Measurement, Measurement>) parent -> {
            double latitude = parent.getPoint().getVector()[0];
            double longitude = parent.getPoint().getVector()[1];

            double roundedLatitude = (double) Math.round(latitude / gridSize) * gridSize;
            double roundedLongitude = (double) Math.round(longitude / gridSize) * gridSize;

            Point roundedPoint = new Point(new Double[]{roundedLatitude, roundedLongitude});
            return new Measurement(roundedPoint, parent.getUuid(), parent.getTimestamp());
        });

        List<Measurement> childMeasurements = roundedMeasRDD.collect();

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
