package net.caspervg.aggr.worker.core.write;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.worker.core.bean.Centroid;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.TimedMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static net.caspervg.aggr.worker.core.util.Constants.*;

/**
 * Implementation fo the {@link AggrWriter} interface that writes data
 * to CSV-formatted files.
 *
 * @implNote Only supports writing actual data (measurements, centroids), not metadata
 */
public class CsvAggrWriter extends FileAggrWriter {

    private static final String[] MEAS_HEADERS = new String[]{DEFAULT_ID_KEY, DEFAULT_LAT_KEY, DEFAULT_LON_KEY,
            DEFAULT_TIMESTAMP_KEY, DEFAULT_SOURCE_KEY, DEFAULT_TYPE_KEY};
    private static final String[] CENT_HEADERS = new String[]{DEFAULT_ID_KEY, DEFAULT_LAT_KEY, DEFAULT_LON_KEY,
            DEFAULT_TIMESTAMP_KEY, DEFAULT_SOURCE_KEY, DEFAULT_TYPE_KEY};
    private Appendable out;

    public CsvAggrWriter(Appendable out) {
        this.out = out;
    }

    @Override
    public void writeMeasurement(Measurement measurement, AggrContext context) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(MEAS_HEADERS).print(out)) {
            printMeasurement(printer, measurement);
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeMeasurements(Iterable<Measurement> measurements, AggrContext context) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(MEAS_HEADERS).print(out)) {
            for (Measurement measurement : measurements) {
                if (measurement instanceof TimedMeasurement) {
                    TimedMeasurement timedMeasurement = (TimedMeasurement) measurement;
                    printMeasurement(printer, timedMeasurement);
                } else {
                    printMeasurement(printer, measurement);
                }
            }
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeCentroid(Centroid centroid, AggrContext context) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(CENT_HEADERS).print(out)) {
            printCentroid(printer, centroid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeCentroids(Iterable<Centroid> centroids, AggrContext context) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(CENT_HEADERS).print(out)) {
            for (Centroid centroid : centroids) {
                printCentroid(printer, centroid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMeasurement(CSVPrinter printer, Measurement measurement) throws IOException {
        printer.printRecord(
                measurement.getUuid(),
                measurement.getPoint().getVector()[0],
                measurement.getPoint().getVector()[1],
                "",
                getParentId(measurement.getParents()),
                "measurement"
        );
    }

    private void printMeasurement(CSVPrinter printer, TimedMeasurement measurement) throws IOException {
        printer.printRecord(
                measurement.getUuid(),
                measurement.getPoint().getVector()[0],
                measurement.getPoint().getVector()[1],
                measurement.getTimestamp().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME),
                getParentId(measurement.getParents()),
                "timed_measurement"
        );
    }

    private void printCentroid(CSVPrinter printer, Centroid centroid) throws IOException {
        printer.printRecord(
                centroid.getUuid(),
                centroid.getPoint().getVector()[0],
                centroid.getPoint().getVector()[1],
                "",
                Arrays.toString(centroid.getMeasurements().stream().map(Measurement::getUuid).toArray()),
                "centroid"
        );
    }

    private String getParentId(Iterable<Measurement> parents) {
        Measurement parent = Iterables.getFirst(parents, null);
        if (parent != null) {
            return parent.getUuid();
        } else {
            return "";
        }
    }
}
