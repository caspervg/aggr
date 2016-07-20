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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlainTimeAggregator extends AbstractTimeAggregator {
    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        TimedMeasurement[] measurementArray = (TimedMeasurement[]) Iterables.toArray(measurements, Measurement.class);
        List<TimedMeasurement> measurementList = Lists.newArrayList(measurementArray);

        // Find the time range
        // Assuming there is at least one measurement in the list!
        LocalDateTime minTimestamp = measurementList.parallelStream().min(new TimedMeasurementComparator()).get().getTimestamp();
        LocalDateTime maxTimestamp = measurementList.parallelStream().max(new TimedMeasurementComparator()).get().getTimestamp();
        long duration = minTimestamp.until(maxTimestamp, ChronoUnit.MILLIS);

        Set<AggregationResult<TimeAggregation, Measurement>> aggregationResults = new HashSet<>();
        int numDetail = Integer.parseInt(context.getParameters().getOrDefault(DETAIL_PARAM, DEFAULT_NUM_DETAIL));

        for (int detail = 1; detail <= numDetail; detail *= 2) {
            // Divide the total time range into multiple steps based on the required detail
            long timeStep = duration / detail;

            for (int i = 0; i < detail; i++) {
                LocalDateTime start = minTimestamp.plus(timeStep * i, ChronoUnit.MILLIS);
                LocalDateTime end = minTimestamp.plus(timeStep * (i + 1), ChronoUnit.MILLIS);

                // Filter only the measurements that fall within the desired time bounds
                List<Measurement> childMeasurements =
                        measurementList
                                .stream()
                                .filter(measurement -> {
                                    LocalDateTime timestamp = measurement.getTimestamp();
                                    return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                                })
                                .map(parent -> {
                                    return new TimedMeasurement(parent.getPoint(), parent.getUuid(), parent.getTimestamp());
                                })
                                .collect(Collectors.toList());

                // Add this aggregation to the result
                aggregationResults.add(new AggregationResult<>(
                        new TimeAggregation(dataset, start, end, childMeasurements),
                        childMeasurements
                ));
            }
        }

        return aggregationResults;
    }
}
