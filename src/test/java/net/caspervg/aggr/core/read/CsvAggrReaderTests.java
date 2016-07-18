package net.caspervg.aggr.core.read;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.util.AggrContext;
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
        params.put("timestamp_key", "timestamp_column");
        params.put("source_key", "parent_column");
        params.put("input_path", CsvAggrReaderTests.class.getResource("/measurements.csv").getPath());

        ctx = new AggrContext(params);
        reader = new CsvAggrReader(new BufferedReader(new InputStreamReader(CsvAggrReader.class.getResourceAsStream("/measurements.csv"))));
    }

    @Test
    public void readOneExistsTest() {
        Optional<Measurement> possibleMeasurement = reader.read("measurement_4", ctx);

        Assert.assertTrue(possibleMeasurement.isPresent());

        Measurement meas = possibleMeasurement.get();
        Assert.assertEquals("measurement_4", meas.getUuid());
        Assert.assertEquals(new Point(new Double[]{50.4,4.4}), meas.getPoint());
        Assert.assertTrue(meas.getParent().isPresent());
        Assert.assertEquals("parent_4", meas.getParent().get());
        Assert.assertEquals(LocalDateTime.parse("2015-09-10T08:47:39"), meas.getTimestamp());

        possibleMeasurement = reader.read("measurement_2", ctx);

        Assert.assertTrue(possibleMeasurement.isPresent());
        meas = possibleMeasurement.get();
        Assert.assertFalse(meas.getParent().isPresent());
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
