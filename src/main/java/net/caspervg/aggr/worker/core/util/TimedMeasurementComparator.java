package net.caspervg.aggr.worker.core.util;

import net.caspervg.aggr.worker.core.bean.TimedMeasurement;

import java.io.Serializable;
import java.util.Comparator;

public class TimedMeasurementComparator implements Comparator<TimedMeasurement>, Serializable {
    @Override
    public int compare(TimedMeasurement o1, TimedMeasurement o2) {
        if(o1 == null && o2 == null) {
            return 0;
        } else if(o1 == null || o1.getTimestamp() == null) {
            return 1;
        } else if(o2 == null || o2.getTimestamp() == null) {
            return -1;
        } else {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }
}
