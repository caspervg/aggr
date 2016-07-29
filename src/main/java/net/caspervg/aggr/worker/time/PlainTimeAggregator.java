package net.caspervg.aggr.worker.time;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.util.TimedMeasurementComparator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlainTimeAggregator extends AbstractTimeAggregator {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                                    Iterable<Measurement> measurements,
                                                                                    AggrContext context) {
        List<Measurement> measurementList = Lists.newArrayList(measurements);

        if (measurementList.size() < 1) {
            return new HashSet<>();
        }

        // Find the time range
        // Assuming there is at least one measurement in the list!
        LocalDateTime minTimestamp = measurementList.parallelStream().min(new TimedMeasurementComparator()).get().getTimestamp().get();
        LocalDateTime maxTimestamp = measurementList.parallelStream().max(new TimedMeasurementComparator()).get().getTimestamp().get();
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
                                    LocalDateTime timestamp = measurement.getTimestamp().get();
                                    return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                                })
                                .map(parent -> {
                                            Measurement child = context.newOutputMeasurement();
                                            child.setVector(parent.getVector());
                                            child.setData(parent.getData());
                                            child.setTimestamp(parent.getTimestamp().get());

                                            return child;
                                        }
                                )
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
