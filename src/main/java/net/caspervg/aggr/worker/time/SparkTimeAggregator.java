package net.caspervg.aggr.worker.time;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.util.TimedMeasurementComparator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SparkTimeAggregator extends AbstractTimeAggregator implements Serializable {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        List<Measurement> measurementList = Lists.newArrayList(measurements);

        JavaSparkContext sparkCtx = context.getSparkContext();
        Class<? extends Measurement> clazz = context.getOutputClass();
        JavaRDD<Measurement> measRDD = sparkCtx.parallelize(measurementList);

        if (measurementList.size() < 1) {
            return new HashSet<>();
        }

        LocalDateTime minTimestamp = measRDD.min(new TimedMeasurementComparator()).getTimestamp().get();
        LocalDateTime maxTimestamp = measRDD.max(new TimedMeasurementComparator()).getTimestamp().get();
        long duration = minTimestamp.until(maxTimestamp, ChronoUnit.MILLIS);

        Set<AggregationResult<TimeAggregation, Measurement>> aggregationResults = new HashSet<>();
        int numDetail = Integer.parseInt(context.getParameters().getOrDefault(DETAIL_PARAM, DEFAULT_NUM_DETAIL));
        for (int detail = 1; detail <= numDetail; detail *= 2) {
            long timeStep = duration / detail;

            for (int i = 0; i < detail; i++) {
                LocalDateTime start = minTimestamp.plus(timeStep * i, ChronoUnit.MILLIS);
                LocalDateTime end = minTimestamp.plus(timeStep * (i + 1), ChronoUnit.MILLIS);

                JavaRDD<Measurement> filteredMeas = measRDD.filter((Function<Measurement, Boolean>) measurement -> {
                    LocalDateTime timestamp = measurement.getTimestamp().get();
                    return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                });

                List<Measurement> childMeasurements = filteredMeas.collect()
                        .stream()
                        .map(parent -> {
                                    Measurement child = newInstance(clazz);
                                    child.setVector(parent.getVector());
                                    child.setData(parent.getData());
                                    child.setTimestamp(parent.getTimestamp().get());

                                    return child;
                                }
                        )
                        .collect(Collectors.toList());

                aggregationResults.add(new AggregationResult<>(
                        new TimeAggregation(dataset, start, end, filteredMeas.collect(), childMeasurements),
                        childMeasurements
                ));
            }
        }

        return aggregationResults;
    }
}
