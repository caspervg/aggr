package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

public class CompositeAggrWriter implements AggrResultWriter {


    private AggrWriter dataWriter;
    private AggrWriter metaWriter;

    public CompositeAggrWriter(AggrWriter writer) {
        this(writer, writer);
    }

    public CompositeAggrWriter(AggrWriter dataWriter, AggrWriter metaWriter) {
        this.dataWriter = dataWriter;
        this.metaWriter = metaWriter;
    }

    @Override
    public void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeKMeansAggregation(AggregationResult<KMeansAggregation, Centroid> result, AggrContext context) {
        dataWriter.writeCentroids(result.getResults(), context);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeTimeAggregation(AggregationResult<TimeAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeDataset(Dataset dataset, AggrContext context) {
        metaWriter.writeDataset(dataset, context);
    }
}
