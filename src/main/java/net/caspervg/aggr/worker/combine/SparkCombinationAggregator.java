package net.caspervg.aggr.worker.combine;

import com.google.common.collect.Lists;
import net.caspervg.aggr.worker.basic.BasicAggregator;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.BasicAggregation;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.spark.api.java.JavaRDD;

import java.util.List;
import java.util.Objects;

@Deprecated
public class SparkCombinationAggregator implements BasicAggregator {
    @Override
    public Iterable<AggregationResult<BasicAggregation, Measurement>> aggregate(Dataset dataset, Iterable<Measurement> measurements, AggrContext context) {
        Objects.requireNonNull(context.getSparkContext());

        List<Measurement> measurementList = Lists.newArrayList(measurements);

        JavaRDD<Measurement> measRDD = context.getSparkContext().parallelize(measurementList);

        return null;
    }
}
