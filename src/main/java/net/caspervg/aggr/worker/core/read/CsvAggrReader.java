package net.caspervg.aggr.worker.core.read;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.Point;
import net.caspervg.aggr.worker.core.bean.TimedMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementation of the {@link AggrReader} interface that
 * reads from a CSV-formatted file.
 *
 * @implNote Expects timestamps to be in {@value DateTimeFormatter#ISO_DATE_TIME} format
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

        String idKey = idKey(params);
        String latKey = latitudeKey(params);
        String lonKey = longitudeKey(params);
        String srcKey = sourceKey(params);
        String timeKey = timestampKey(params);

        String id = null;
        if (record.isSet(idKey)) {
            id = record.get(idKey);
        }
        String latStr = record.get(latKey);
        String lonStr = record.get(lonKey);
        String timeStr = record.get(timeKey);

        if (latStr == null || lonStr == null || timeStr == null) {
            throw new IllegalStateException("Latitude, longitude and time columns may not be empty: " + record.toString());
        }

        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);
        LocalDateTime time = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME);
        String parent = null;
        if (record.isSet(srcKey)) {
            parent = record.get(srcKey);
        }

        Point point = new Point(new Double[]{lat, lon});
        TimedMeasurement.Builder builder = TimedMeasurement.Builder
                .setup()
                .withPoint(point)
                .withTimestamp(time);

        if (parent != null) {
            builder = builder.withParent(parent);
        }
        if (id != null) {
            builder = builder.withUuid(id);
        }

        return builder.build();
    }

    private Iterable<CSVRecord> parseRecords(Reader in) throws IOException {
        return CSVFormat.DEFAULT.withHeader().parse(in);
    }
}