package net.caspervg.aggr.aggregation.average;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.caspervg.aggr.core.bean.Combinable;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.aggregation.AggregationResult;
import net.caspervg.aggr.core.util.AggrContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * {@inheritDoc}
 *
 * @implNote Uses Spark to perform the aggregation
 */
public class SparkAverageAggregator extends AbstractAverageAggregator implements Serializable {

    public SparkAverageAggregator() {
        super();
    }

    public SparkAverageAggregator(Iterable<Iterable<Measurement>> others) {
        super(others);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<AggregationResult<AverageAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> ignored, AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        if (Iterables.isEmpty(others)) {
            throw new IllegalStateException("Collection of others may not be empty");
        }

        long amount = Long.parseLong(
                context.getParameters().getOrDefault(AMOUNT_PARAM_KEY, defaultAmount())
        );
        String key = context.getParameters().getOrDefault(KEY_PARAM_KEY, DEFAULT_KEY);

        JavaSparkContext jsc = context.getSparkContext();
        Class<? extends Measurement> clazz = context.getOutputClass();

        String othersFileNames = context.getParameters().get(OTHERS_PARAM_KEY);

        JavaRDD<Measurement> othersRDD = jsc.parallelize(Lists.newArrayList(Iterables.concat(others)));
        JavaPairRDD<Integer, Iterable<Measurement>> otherGroups = othersRDD.groupBy((Function<Measurement, Integer>) Combinable::combinationHash);
        JavaPairRDD<Integer, Double> averages = otherGroups
                .mapValues(new Function<Iterable<Measurement>, Double>() {
                    @Override
                    public Double call(Iterable<Measurement> measurements) throws Exception {
                        return StreamSupport
                                .stream(measurements.spliterator(), true)
                                .mapToDouble(measurement -> getValue(key, measurement))
                                .sum();
                    }
                })
                .mapValues((Function<Double, Double>) sum -> sum / amount);


        Map<Integer, Iterable<Measurement>> groupMap = otherGroups.collectAsMap();

        JavaRDD<Measurement> avgMeasRDD = averages.map(new Function<Tuple2<Integer, Double>, Measurement>() {
            @Override
            public Measurement call(Tuple2<Integer, Double> tuple) throws Exception {
                int tupleKey = tuple._1;
                double tupleValue = tuple._2;

                Measurement average = newInstance(clazz);
                Measurement representative = Iterables.get(groupMap.get(tupleKey), 0);

                average.setData(representative.getData());
                average.setVector(representative.getVector());
                average.setDatum(key, tupleValue);

                // Get the parents used to calculate the average
                Set<UniquelyIdentifiable> parentSet = Sets.newHashSet(groupMap.getOrDefault(tupleKey, Sets.newHashSet()));

                average.setParents(parentSet);

                return average;
            }
        });

        List<Measurement> averageMeas = avgMeasRDD.collect();

        return Lists.newArrayList(
                new AggregationResult<>(
                        new AverageAggregation(
                                dataset,
                                othersFileNames,
                                amount,
                                key,
                                Lists.newArrayList(Iterables.concat(others)),
                                averageMeas
                        ),
                        averageMeas
                )
        );
    }

    private double getValue(String key, Measurement measurement) {
        return Double.valueOf(String.valueOf(measurement.getDatum(key).orElse(0.0)));
    }
}
