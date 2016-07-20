package net.caspervg.aggr.time;

import net.caspervg.aggr.core.AbstractAggregationExecution;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.read.AbstractAggrReader;
import net.caspervg.aggr.core.util.AggrCommand;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.write.AggrResultWriter;
import net.caspervg.aggr.time.util.TimeAggrCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class TimeAggregationExecution extends AbstractAggregationExecution {

    private AggrCommand ac;
    private TimeAggrCommand tac;

    public TimeAggregationExecution(AggrCommand ac, TimeAggrCommand tac) {
        this.ac = ac;
        this.tac = tac;
    }

    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractTimeAggregator.DETAIL_PARAM, String.valueOf(tac.getMaxDetail()));

        AggrContext ctx;
        TimeAggregator aggregator;
        if (ac.isSpark()) {
            String hdfsUrl = ac.getHdfsUrl();
            JavaSparkContext sparkCtx = getSparkContext(ac);

            if (StringUtils.isNotBlank(hdfsUrl)) {
                FileSystem hdfs = FileSystem.get(new URI(hdfsUrl), sparkCtx.hadoopConfiguration());
                ctx = new AggrContext(params, sparkCtx, hdfs);
            } else {
                ctx = new AggrContext(params, sparkCtx);
            }
            aggregator = new SparkTimeAggregator();
        } else {
            ctx = new AggrContext(params);
            aggregator = new PlainTimeAggregator();
        }

        Dataset dataset = new Dataset("time_dataset");
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<TimeAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<TimeAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeTimeAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);

    }
}
