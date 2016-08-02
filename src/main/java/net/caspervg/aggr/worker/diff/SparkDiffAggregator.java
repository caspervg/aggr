package net.caspervg.aggr.worker.diff;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.worker.core.bean.Combinable;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.DiffAggregation;
import net.caspervg.aggr.worker.core.bean.impl.WeightedGeoMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.*;

public class SparkDiffAggregator extends AbstractDiffAggregator implements Serializable {

    public SparkDiffAggregator() {
        super();
    }

    public SparkDiffAggregator(Iterable<Iterable<Measurement>> subtrahends) {
        super(subtrahends);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        if (Iterables.isEmpty(others)) {
            throw new IllegalStateException("Collection of subtrahends may not be empty");
        }

        JavaSparkContext jsc = context.getSparkContext();
        Class<? extends Measurement> clazz = context.getOutputClass();

        String subtrahendFileNames = context.getParameters().get(SUBTRAHEND_PARAM_KEY);

        JavaRDD<Measurement> othersRDD = jsc.parallelize(Lists.newArrayList(Iterables.concat(others)));
        JavaPairRDD<Integer, Iterable<Measurement>> otherGroups = othersRDD.groupBy((Function<Measurement, Integer>) Combinable::combinationHash);
        JavaPairRDD<Integer, Long> averages = otherGroups
                .mapValues(new Function<Iterable<Measurement>, Double>() {
                    @Override
                    public Double call(Iterable<Measurement> measurements) throws Exception {
                        List<Measurement> group = Lists.newArrayList(measurements);
                        return group
                                .stream()
                                .mapToLong(measurement -> getWeight(measurement))
                                .average()
                                .getAsDouble();
                    }
                })
                .mapValues(Double::longValue);

        Map<Integer, Iterable<Measurement>> groupMap = otherGroups.collectAsMap();
        Map<Integer, Long> averageMap = averages.collectAsMap();

        JavaRDD<Measurement> measRDD = jsc.parallelize(Lists.newArrayList(measurements));

        JavaRDD<Measurement> diffRDD = measRDD.map(new Function<Measurement, Measurement>() {
            @Override
            public Measurement call(Measurement measurement) throws Exception {
                Measurement difference = newInstance(clazz);

                int hash = measurement.combinationHash();

                difference.setData(measurement.getData());
                difference.setVector(measurement.getVector());

                long avgWt = averageMap.getOrDefault(hash, 0L);
                long ownWt = getWeight(measurement);
                long diffWt = ownWt - avgWt;

                difference.setDatum(WeightedGeoMeasurement.WEIGHT_KEY, diffWt);

                // Get the parents used to calculate the average
                Set<UniquelyIdentifiable> parentSet = Sets.newHashSet(groupMap.getOrDefault(hash, Sets.newHashSet()));
                // Add the source measurement itself
                parentSet.add(measurement);

                difference.setParents(parentSet);

                return difference;
            }
        });

        List<Measurement> diffs = diffRDD.collect();

        return Lists.newArrayList(
                new AggregationResult<>(
                        new DiffAggregation(
                                dataset,
                                subtrahendFileNames,
                                Lists.newArrayList(measurements),
                                diffs
                        ),
                        diffs
                )
        );
    }

    private long getWeight(Measurement measurement) {
        return Long.valueOf(String.valueOf(measurement.getDatum(WeightedGeoMeasurement.WEIGHT_KEY).get()));
    }
}
