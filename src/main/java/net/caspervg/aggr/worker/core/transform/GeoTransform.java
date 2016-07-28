package net.caspervg.aggr.worker.core.transform;

import java.util.Map;

import static net.caspervg.aggr.worker.core.util.Constants.DEFAULT_LAT_KEY;
import static net.caspervg.aggr.worker.core.util.Constants.DEFAULT_LON_KEY;

public class GeoTransform implements Transform<Map<String, Object>> {
    @Override
    public double[] transform(Map<String, Object> source) {
        return new double[] {
                (double) source.getOrDefault(DEFAULT_LAT_KEY, 0.0),
                (double) source.getOrDefault(DEFAULT_LON_KEY, 0.0)
        };
    }
}
