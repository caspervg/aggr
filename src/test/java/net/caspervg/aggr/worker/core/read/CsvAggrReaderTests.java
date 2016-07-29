package net.caspervg.aggr.worker.core.read;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.impl.TimedGeoMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CsvAggrReaderTests {

    private AggrContext ctx;
    private CsvAggrReader reader;

    @Before
    public void initialize() {
        Map<String, String> params = new HashMap<>();

        params.put("id_key", "identifier");
        params.put("source_key", "parent_column");
        params.put("input_path", CsvAggrReaderTests.class.getResource("/measurements.csv").getPath());

        ctx = AggrContext.builder().parameters(params).inputClass(TimedGeoMeasurement.class).build();
        reader = new CsvAggrReader(new BufferedReader(new InputStreamReader(CsvAggrReader.class.getResourceAsStream("/measurements.csv"))));
    }

    @Test
    public void readOneExistsTest() {
        Optional<Measurement> possibleMeasurement = reader.read("measurement_4", ctx);

        Assert.assertTrue(possibleMeasurement.isPresent());

        Measurement meas = possibleMeasurement.get();
        Assert.assertEquals("measurement_4", meas.getUuid());
        Assert.assertArrayEquals(new Double[]{50.4,4.4}, meas.getVector());
        Assert.assertEquals(Optional.of(LocalDateTime.parse("2015-09-10T08:47:39")), meas.getTimestamp());
    }

    @Test
    public void readOneNotExistsTest() {
        Optional<Measurement> possibleMeasurement = reader.read("measurement_unavailable", ctx);
        Assert.assertFalse(possibleMeasurement.isPresent());
    }

    @Test
    public void readAllTest() {
        Iterable<Measurement> measurements = reader.read(ctx);
        Assert.assertNotNull(measurements);

        Measurement[] measArr = Iterables.toArray(measurements, Measurement.class);
        Assert.assertEquals(4, measArr.length);
    }
}
