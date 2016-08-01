package net.caspervg.aggr.worker.core.util;

import net.caspervg.aggr.worker.core.bean.Measurement;

import java.util.Comparator;

public class MeasurementVectorComparator implements Comparator<Measurement> {
    @Override
    public int compare(Measurement o1, Measurement o2) {
        Double[] vec1 = o1.getVector();
        Double[] vec2 = o2.getVector();

        if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectors must be of the same dimension");

        for (int i = 0; i < vec1.length; i++) {
            if (vec1[i] < vec2[i]) {
                return -1;
            } else if (vec1[i] > vec2[i]) {
                return 1;
            }
        }

        return 0;
    }
}
