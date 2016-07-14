package net.caspervg.aggr.time;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.MeasurementTimeComparator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SparkTimeAggregator implements TimeAggregator, Serializable {
    @Override
    public Iterable<AggregationResult<TimeAggregation, Measurement>> aggregate(Dataset dataset,
                                                                               Iterable<Measurement> measurements,
                                                                               AggrContext context) {

        JavaSparkContext sparkCtx = context.getSparkContext();
        JavaRDD<Measurement> measRDD = sparkCtx.parallelize(Lists.newArrayList(measurements));

        LocalDateTime minTimestamp = measRDD.min(new MeasurementTimeComparator()).getTimestamp();
        LocalDateTime maxTimestamp = measRDD.max(new MeasurementTimeComparator()).getTimestamp();
        long duration = minTimestamp.until(maxTimestamp, ChronoUnit.MILLIS);

        Set<AggregationResult<TimeAggregation, Measurement>> aggregationResults = new HashSet<>();
        int numDetail = Integer.parseInt(context.getParameters().getOrDefault("detail", "8"));
        for (int detail = 1; detail <= numDetail; detail *= 2) {
            long timeStep = duration / detail;

            for (int i = 0; i < detail; i++) {
                LocalDateTime start = minTimestamp.plus(timeStep * i, ChronoUnit.MILLIS);
                LocalDateTime end = minTimestamp.plus(timeStep * (i + 1), ChronoUnit.MILLIS);

                JavaRDD<Measurement> filteredMeas = measRDD.filter(new Function<Measurement, Boolean>() {
                    @Override
                    public Boolean call(Measurement measurement) throws Exception {
                        LocalDateTime timestamp = measurement.getTimestamp();
                        return (timestamp.isEqual(start) || (timestamp.isAfter(start) && timestamp.isBefore(end)));
                    }
                });

                List<Measurement> childMeasurements = filteredMeas.collect()
                        .stream()
                        .map(parent ->
                                new Measurement(parent.getPoint(), parent.getUuid(), parent.getTimestamp()))
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
