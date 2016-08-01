package net.caspervg.aggr.worker.core.transform;

@FunctionalInterface
public interface Transform<T> {
    double[] transform(T source);
}
