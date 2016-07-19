package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AggregationResult;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of the {@link AggrResultWriter} interface that supports
 * writing metadata (e.g. provenance, used parameters, ...) to a separate channel
 * than the actual data (computed aggregation results).
 */
public class CompositeAggrWriter implements AggrResultWriter {

    private AggrWriter dataWriter;
    private AggrWriter metaWriter;
    private boolean writeProvenance;

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
        this.dataWriter = dataWriter;
        this.metaWriter = metaWriter;
        this.writeProvenance = writeProvenance;
    }

    @Override
    public void writeGridAggregation(AggregationResult<GridAggregation, Measurement> result, AggrContext context) {
        dataWriter.writeMeasurements(result.getResults(), context);
        metaWriter.writeAggregation(result.getAggregation(), context);
    }

    @Override
    public void writeKMeansAggregation(AggregationResult<KMeansAggregation, Centroid> result, AggrContext context) {
        Iterable<Centroid> centroids = result.getResults();
        dataWriter.writeCentroids(centroids, context);
        metaWriter.writeAggregation(result.getAggregation(), context);

        if (writeProvenance) {
            for (Centroid centroid : centroids) {
                metaWriter.writeMeasurements(centroid.getMeasurements(), context);
            }
        }
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
