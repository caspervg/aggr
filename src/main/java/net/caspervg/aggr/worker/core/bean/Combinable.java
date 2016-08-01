package net.caspervg.aggr.worker.core.bean;

public interface Combinable {
    /**
     * Checks if this measurement is eligible for combination with the given measurement.
     *
     * @param other Measurement to check for combination possibility.
     * @return <code>true</code> if combination is possible,
     *         <code>false</code> otherwise
     */
    boolean canCombine(Measurement other);

    /**
     * Combines this measurement with the given measurement, returning at least a
     * new instance of the {@link Measurement} interface with the callee and the given measurement as it's parents.
     *
     * This combined instance must have a fresh UUID attached to it.
     *
     * @param other Measurement to combine with the callee.
     * @return Combined measurement.
     * @throws IllegalArgumentException if {{@link #canCombine(Measurement)}} with the given measurement
     * returns <code>false</code>, indicating that the combination is not possible.
     */
    Measurement combine(Measurement other);

    Measurement combine(Iterable<Measurement> others);
}
