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
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

public class SparkDiffAggregator extends AbstractDiffAggregator implements Serializable {

    public SparkDiffAggregator() {
        super();
    }

    public SparkDiffAggregator(Iterable<Measurement> subtrahends) {
        super(subtrahends);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<AggregationResult<DiffAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> minuends, AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        String key = context.getParameters().getOrDefault(KEY_PARAM_KEY, DEFAULT_KEY);

        JavaSparkContext jsc = context.getSparkContext();
        Class<? extends Measurement> clazz = context.getOutputClass();

        String subtrahendFileName = context.getParameters().get(OTHER_PARAM_KEY);

        JavaRDD<Measurement> subtrahendRDD = jsc.parallelize(Lists.newArrayList(subtrahends));
        JavaPairRDD<Integer, Iterable<Measurement>> subtrahendGroupsRDD = subtrahendRDD.groupBy((Function<Measurement, Integer>) Combinable::combinationHash);
        JavaPairRDD<Integer, Double> sums = subtrahendGroupsRDD
                .mapValues(new Function<Iterable<Measurement>, Double>() {
                    @Override
                    public Double call(Iterable<Measurement> measurements) throws Exception {
                        return StreamSupport
                                .stream(measurements.spliterator(), true)
                                .mapToDouble(subtrahend -> getValue(key, subtrahend))
                                .sum();
                    }
                });

        Map<Integer, Iterable<Measurement>> groupMap = subtrahendGroupsRDD.collectAsMap();
        Map<Integer, Double> sumMap = sums.collectAsMap();

        JavaRDD<Measurement> minuendRDD = jsc.parallelize(Lists.newArrayList(minuends));

        JavaRDD<Measurement> diffMeasRDD = minuendRDD.map(new Function<Measurement, Measurement>() {
            @Override
            public Measurement call(Measurement minuend) throws Exception {
                Measurement diff = newInstance(clazz);

                int hash = minuend.combinationHash();

                diff.setData(minuend.getData());
                diff.setVector(minuend.getVector());

                double subtrahendVal = sumMap.getOrDefault(hash, 0.0);
                double minuendVal = getValue(key, minuend);
                double diffVal = minuendVal - subtrahendVal;

                diff.setDatum(key, diffVal);

                // Get the parents used to calculate the average
                Set<UniquelyIdentifiable> parentSet = Sets.newHashSet(groupMap.getOrDefault(hash, Sets.newHashSet()));
                // Add the source measurement itself
                parentSet.add(minuend);

                diff.setParents(parentSet);

                return diff;
            }
        });

        List<Measurement> diffMeas = diffMeasRDD.collect();

        return Lists.newArrayList(
                new AggregationResult<>(
                        new DiffAggregation(
                                dataset,
                                subtrahendFileName,
                                key,
                                Lists.newArrayList(Iterables.concat(subtrahends, minuends)),
                                diffMeas
                        ),
                        diffMeas
                )
        );
    }

    private double getValue(String key, Measurement measurement) {
        return Double.valueOf(String.valueOf(measurement.getDatum(key).orElse(0.0)));
    }
}
