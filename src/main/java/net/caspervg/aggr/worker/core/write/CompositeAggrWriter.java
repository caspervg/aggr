package net.caspervg.aggr.worker.core.write;

import net.caspervg.aggr.worker.core.bean.Dataset;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.aggregation.*;
import net.caspervg.aggr.worker.core.util.AggrContext;

/**
 * Implementation of the {@link AggrResultWriter} interface that supports
 * writing metadata (e.g. provenance, used parameters, ...) to a separate channel
 * than the actual data (computed aggregation results).
 */
public class CompositeAggrWriter implements AggrResultWriter {

    private AggrWriter dataWriter;
    private AggrWriter metaWriter;
    private boolean writeProvenance;
    private String dataPath;

    /**
     * Create a new CompositeAggrWriter that will write all
     * information to the same channel.
     *
     * @param writer Channel to send all data to
     */
    public CompositeAggrWriter(AggrWriter writer) {
        this(writer, writer);
    }

    /**
     * Create a new CompositeAggrWriter that will send information
     * to different channels.
     *
     * @param dataWriter Channel to use for actual data
     * @param metaWriter Channel to use for metadata
     */
    public CompositeAggrWriter(AggrWriter dataWriter, AggrWriter metaWriter) {
        this(dataWriter, metaWriter, false);
    }

    public CompositeAggrWriter(AggrWriter dataWriter, AggrWriter metaWriter, boolean writeProvenance) {
        this(dataWriter, metaWriter, writeProvenance, "triples");
    }

    public CompositeAggrWriter(AggrWriter dataWriter, AggrWriter metaWriter, boolean writeProvenance, String dataPath) {
        this.dataWriter = dataWriter;
        this.metaWriter = metaWriter;
        this.writeProvenance = writeProvenance;
        this.dataPath = dataPath;
    }

    @Override
    public void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeKMeansAggregation(AggregationResult<KMeansAggregation, Measurement> result, AggrContext context) {
        Iterable<Measurement> centroids = result.getResults();
        dataWriter.writeMeasurements(centroids, context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeTimeAggregation(AggregationResult<TimeAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeBasicAggregation(AggregationResult<BasicAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeDiffAggregation(AggregationResult<DiffAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeAverageAggregation(AggregationResult<AverageAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        result.getAggregation().setDataPath(dataPath);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeDataset(Dataset dataset, AggrContext context) {
        metaWriter.writeDataset(dataset, context);
    }
}
