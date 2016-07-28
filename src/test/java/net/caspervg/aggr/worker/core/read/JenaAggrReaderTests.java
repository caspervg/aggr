/*
package net.caspervg.aggr.worker.core.read;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.Point;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JenaAggrReaderTests {
    private AggrContext ctx;
    private AggrReader reader;

    @Before
    public void initialize() {
        Map<String, String> params = new HashMap<>();

        params.put("service", "mem:dataset=src/test/resources/measurements.ttl");
        //params.put("service", "http://localhost:8890/sparql");

        ctx = new AggrContext(params);
        reader = new JenaAggrReader();
    }

    @Test
    public void readOneExistsTest() {
        Optional<Measurement> possibleMeasurement = reader.read("measurement_4", ctx);

        Assert.assertTrue(possibleMeasurement.isPresent());

        Measurement meas = possibleMeasurement.get();
        Assert.assertEquals("measurement_4", meas.getUuid());
        Assert.assertEquals(new Point(new Double[]{50.4,4.4}), meas.getPoint());
*/
/*        Assert.assertTrue(meas.getParent().isPresent());
        Assert.assertEquals("parent_4", meas.getParent().get());
        Assert.assertEquals(LocalDateTime.parse("2015-09-10T08:47:39"), meas.getTimestamp());

        possibleMeasurement = reader.read("measurement_2", ctx);

        Assert.assertTrue(possibleMeasurement.isPresent());
        meas = possibleMeasurement.get();
        Assert.assertFalse(meas.getParent().isPresent());*//*

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
*/
