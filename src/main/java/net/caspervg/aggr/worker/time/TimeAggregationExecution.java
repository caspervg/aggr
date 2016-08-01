package net.caspervg.aggr.worker.time;

import net.caspervg.aggr.worker.core.AbstractAggregationExecution;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.worker.core.read.AbstractAggrReader;
import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.write.AggrResultWriter;
import net.caspervg.aggr.core.TimeAggrCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.worker.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

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

        TimeAggregator aggregator;
        if (ac.isSpark()) {
            aggregator = new SparkTimeAggregator();
        } else {
            aggregator = new PlainTimeAggregator();
        }
        AggrContext ctx = createContext(params, ac);

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
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
