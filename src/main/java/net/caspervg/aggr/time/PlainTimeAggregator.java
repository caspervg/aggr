package net.caspervg.aggr.time;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.MeasurementTimeComparator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlainTimeAggregator implements TimeAggregator {
    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        List<Measurement> measurementList = Lists.newArrayList(measurements);

        // Assuming there is at least one measurement in the list!
        LocalDateTime minTimestamp = measurementList.parallelStream().min(new MeasurementTimeComparator()).get().getTimestamp();
        LocalDateTime maxTimestamp = measurementList.parallelStream().max(new MeasurementTimeComparator()).get().getTimestamp();
        long duration = minTimestamp.until(maxTimestamp, ChronoUnit.MILLIS);

        Set<AggregationResult<TimeAggregation, Measurement>> aggregationResults = new HashSet<>();
        int numDetail = Integer.parseInt(context.getParameters().getOrDefault("detail", "8"));

        for (int detail = 1; detail <= numDetail; detail *= 2) {
            long timeStep = duration / detail;

            for (int i = 0; i < detail; i++) {
                LocalDateTime start = minTimestamp.plus(timeStep * i, ChronoUnit.MILLIS);
                LocalDateTime end = minTimestamp.plus(timeStep * (i + 1), ChronoUnit.MILLIS);

                List<Measurement> childMeasurements =
                        measurementList
                                .stream()
                                .filter(measurement -> {
                                    LocalDateTime timestamp = measurement.getTimestamp();
                                    return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                                })
                                .map(parent -> {
                                    return new Measurement(parent.getPoint(), parent.getUuid(), parent.getTimestamp());
                                })
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
