package net.caspervg.aggr.worker.write;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.core.util.AggrContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.caspervg.aggr.core.util.Constants.DEFAULT_ID_KEY;
import static net.caspervg.aggr.core.util.Constants.DEFAULT_SOURCE_KEY;

/**
 * Implementation fo the {@link AggrWriter} interface that writes data
 * to CSV-formatted files.
 *
 * @implNote Only supports writing actual data (measurements, centroids), not metadata
 */
public class CsvAggrWriter extends FileAggrWriter {

    private Appendable out;

    public CsvAggrWriter(Appendable out) {
        this.out = out;
    }

    @Override
    public void writeMeasurement(Measurement measurement, AggrContext context) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(getMeasurementHeaders(measurement)).print(out)) {
            printMeasurement(printer, measurement);
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeMeasurements(Iterable<Measurement> measurements, AggrContext context) {
        if (Iterables.isEmpty(measurements)) return;

        try (CSVPrinter printer = CSVFormat.DEFAULT.withHeader(getMeasurementHeaders(measurements)).print(out)) {
            for (Measurement measurement : measurements) {
                printMeasurement(printer, measurement);
            }
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getMeasurementHeaders(Iterable<Measurement> measurements) {
        return getMeasurementHeaders(Iterables.get(measurements, 0));
    }

    private String[] getMeasurementHeaders(Measurement measurement) {
        List<String> headerList = Lists.newArrayList(DEFAULT_ID_KEY, DEFAULT_SOURCE_KEY);
        headerList.addAll(measurement.getWriteKeys());
        return headerList.toArray(new String[]{});
    }

    private void printMeasurement(CSVPrinter printer, Measurement measurement) throws IOException {
        List<Object> recordList = Lists.newArrayList(measurement.getUuid(),
                getParentIds(measurement.getParents()));
        Map<String, Object> data = measurement.getData();
        for (String key : measurement.getWriteKeys()) {
            recordList.add(data.get(key));
        }
        printer.printRecord(
                recordList.toArray()
        );
    }

    private String getParentIds(Set<UniquelyIdentifiable> parents) {
        if (Iterables.isEmpty(parents)) {
            return "[]";
        } else {
            return Arrays.toString(parents.stream().map(UniquelyIdentifiable::getUuid).toArray());
        }
    }
}
