package net.caspervg.aggr.core.bean.util;

import net.caspervg.aggr.core.bean.Measurement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class TimedMeasurementComparator implements Comparator<Measurement>, Serializable {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public int compare(Measurement o1, Measurement o2) {

        if(o1 == null && o2 == null) {
            return 0;
        } else if(o1 == null || !o1.getTimestamp().isPresent()) {
            return 1;
        } else if(o2 == null || !o2.getTimestamp().isPresent()) {
            return -1;
        } else {
            Optional<LocalDateTime> o1t = o1.getTimestamp();
            Optional<LocalDateTime> o2t = o2.getTimestamp();

            return o1t.get().compareTo(o2t.get());
        }
    }
}
