package net.caspervg.aggr.worker.core.transform;

import org.apache.commons.lang3.ArrayUtils;

public class IdempotentVectorTransform implements Transform<Double[]>{

    @Override
    public double[] transform(Double[] source) {
        return ArrayUtils.toPrimitive(source);
    }
}
