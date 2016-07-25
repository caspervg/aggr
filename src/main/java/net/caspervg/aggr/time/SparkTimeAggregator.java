package net.caspervg.aggr.time;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.TimedMeasurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.TimedMeasurementComparator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class SparkTimeAggregator extends AbstractTimeAggregator implements Serializable {

    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        Measurement[] measurementArray = Iterables.toArray(measurements, Measurement.class);
        TimedMeasurement[] timedMeasurementArray = Arrays.copyOf(measurementArray, measurementArray.length, TimedMeasurement[].class);
        List<TimedMeasurement> measurementList = Lists.newArrayList(timedMeasurementArray);

        Objects.requireNonNull(context.getSparkContext());

        JavaSparkContext sparkCtx = context.getSparkContext();
        JavaRDD<TimedMeasurement> measRDD = sparkCtx.parallelize(measurementList);

        if (measurementList.size() < 1) {
            return new HashSet<>();
        }

        LocalDateTime minTimestamp = measRDD.min(new TimedMeasurementComparator()).getTimestamp();
        LocalDateTime maxTimestamp = measRDD.max(new TimedMeasurementComparator()).getTimestamp();
        long duration = minTimestamp.until(maxTimestamp, ChronoUnit.MILLIS);

        Set<AggregationResult<TimeAggregation, Measurement>> aggregationResults = new HashSet<>();
        int numDetail = Integer.parseInt(context.getParameters().getOrDefault(DETAIL_PARAM, DEFAULT_NUM_DETAIL));
        for (int detail = 1; detail <= numDetail; detail *= 2) {
            long timeStep = duration / detail;

            for (int i = 0; i < detail; i++) {
                LocalDateTime start = minTimestamp.plus(timeStep * i, ChronoUnit.MILLIS);
                LocalDateTime end = minTimestamp.plus(timeStep * (i + 1), ChronoUnit.MILLIS);

                JavaRDD<TimedMeasurement> filteredMeas = measRDD.filter((Function<TimedMeasurement, Boolean>) measurement -> {
                    LocalDateTime timestamp = measurement.getTimestamp();
                    return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                });

                List<Measurement> childMeasurements = filteredMeas.collect()
                        .stream()
                        .map(parent ->
                                TimedMeasurement.Builder
                                    .setup()
                                    .withPoint(parent.getPoint())
                                    .withParent(parent)
                                    .withTimestamp(parent.getTimestamp())
                                    .build()
                        )
                        .collect(Collectors.toList());

                aggregationResults.add(new AggregationResult<>(
                        new TimeAggregation(dataset, start, end, childMeasurements),
                        childMeasurements
                ));
            }
        }

        return aggregationResults;
    }
}
