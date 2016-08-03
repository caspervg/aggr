package net.caspervg.aggr.core.bean;

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
     * Returns a hash of the combination of data points that are required for another
     * {@link Measurement} to be combined with this measurement.
     *
     * In other words, if {@code this.canCombine(other)} then {@code this.combinationHash() == other.combinationHash()}
     *
     * @return A hash identifying the data points that are required for compatibility.
     */
    int combinationHash();

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
