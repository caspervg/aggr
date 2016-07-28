package net.caspervg.aggr.worker.core.read;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.worker.core.bean.impl.BasicParent;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Implementation of the {@link AggrReader} interface that
 * reads from a CSV-formatted file.
 */
public class CsvAggrReader extends AbstractAggrReader {

    private Reader in;

    /**
     * Creates a new CsvAggrReader that will read from given {@link Reader}
     *
     * @param in Reader to use
     */
    public CsvAggrReader(Reader in) {
        this.in = in;
    }

    @Override
    public Optional<Measurement> read(String id, AggrContext context) {
        Map<String, String> params = context.getParameters();
        String idKey = idKey(params);

        try {
            Iterable<CSVRecord> records = parseRecords(this.in);

            for (CSVRecord record : records) {
                if (id.equals(record.get(idKey))) {
                    return Optional.of(measurementFromRecord(context, record));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Measurement> read(AggrContext context) {
        Set<Measurement> measurements = new HashSet<>();

        try {
            Iterable<CSVRecord> records = parseRecords(this.in);

            for (CSVRecord record : records) {
                measurements.add(measurementFromRecord(context, record));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return measurements;
    }

    private Measurement measurementFromRecord(AggrContext context, CSVRecord record) {
        Map<String, String> params = context.getParameters();
        Measurement measurement = context.newMeasurement();

        String idKey = idKey(params);
        String srcKey = sourceKey(params);

        if (record.isSet(idKey)) {
            String measId = record.get(idKey);
            measurement.setUuid(measId);
        }

        if (record.isSet(srcKey)) {
            Set<UniquelyIdentifiable> parents = new HashSet<>();
            String parentId = record.get(srcKey);

            parents.add(new BasicParent(parentId));
            measurement.setParents(parents);
        }

        Map<String, Object> data = new HashMap<>();
        for (String key : measurement.getReadKeys()) {
            data.put(key, record.get(key));
        }
        measurement.setData(data);

        return measurement;
    }

    private Iterable<CSVRecord> parseRecords(Reader in) throws IOException {
        return CSVFormat.DEFAULT.withHeader().parse(in);
    }
}
