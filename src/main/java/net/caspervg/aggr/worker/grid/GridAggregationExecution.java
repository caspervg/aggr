package net.caspervg.aggr.worker.grid;

import net.caspervg.aggr.core.AggrCommand;
import net.caspervg.aggr.core.GridAggrCommand;
import net.caspervg.aggr.worker.core.AbstractAggregationExecution;
import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.worker.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.worker.core.read.AbstractAggrReader;
import net.caspervg.aggr.worker.core.util.AggrContext;
import net.caspervg.aggr.worker.core.write.AggrResultWriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static net.caspervg.aggr.worker.core.write.AbstractAggrWriter.OUTPUT_PARAM_KEY;

public class GridAggregationExecution extends AbstractAggregationExecution {

    private AggrCommand ac;
    private GridAggrCommand gac;

    public GridAggregationExecution(AggrCommand ac, GridAggrCommand gac) {
        this.ac = ac;
        this.gac = gac;
    }

    @Override
    public void execute() throws URISyntaxException, IOException {
        Map<String, String> params = ac.getDynamicParameters();
        params.put(AbstractAggrReader.INPUT_PARAM_KEY, ac.getInput());
        params.put(OUTPUT_PARAM_KEY, ac.getOutput());
        params.put(AbstractGridAggregator.GRID_SIZE_PARAM, String.valueOf(gac.getGridSize()));

        AggrContext ctx = createContext(params, ac);
        GridAggregator aggregator;
        if (ac.isSpark()) {
            aggregator = new SparkGridAggregator();
        } else {
            aggregator = new PlainGridAggregator();
        }

        Dataset dataset = Dataset.Builder.setup().withTitle(ac.getDatasetId()).withUuid(ac.getDatasetId()).build();
        Iterable<Measurement> meas = getReader(ac, ctx).read(ctx);
        Iterable<AggregationResult<GridAggregation, Measurement>> results = aggregator.aggregate(dataset, meas, ctx);

        AggrResultWriter writer = null;
        for (AggregationResult<GridAggregation, Measurement> res : results) {
            writer = getWriter(res, ac, ctx);

            writer.writeGridAggregation(res, ctx);
        }

        if (writer != null) {
            writer.writeDataset(dataset, ctx);
        }

        stop(ctx);

    }
}
