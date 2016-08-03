package net.caspervg.aggr.core.distance;

/**
 * Implementation of the {@link DistanceMetric} interface that uses the
 * <a href="https://en.wikipedia.org/wiki/Karlsruhe_metric">Karlsruhe distance</a> metric.
 *
 * <p>
 *     <b>Note:</b>
 *     <ul>
 *         <li>
 *             This metric only supports vectors of length 2
 *         </li>
 *         <li>
 *             This metric takes a long time to calculate
 *         </li>
 *     </ul>
 * </p>
 *
 * @param <T> Type of the vectors to calculate distance between.
 */
public class KarlsruheDistanceMetric<T extends Number> extends AbstractDistanceMetric<T> {
    @Override
    public double distance(T[] vector1, T[] vector2) {
        super.checkArguments(vector1, vector2);

        if (vector1.length != 2) {
            throw new IllegalArgumentException("Both vectors must have length 2");
        }

        double rP = Math.sqrt(Math.pow(vector1[0].doubleValue(), 2) + Math.pow(vector1[1].doubleValue(), 2));
        double phiP = Math.atan2(vector1[1].doubleValue(), vector1[0].doubleValue());
        double rQ = Math.sqrt(Math.pow(vector2[0].doubleValue(), 2) + Math.pow(vector2[1].doubleValue(), 2));
        double phiQ = Math.atan2(vector2[1].doubleValue(), vector2[0].doubleValue());

        double delta = Math.min(
                Math.abs(phiP - phiQ),
                2*Math.PI - Math.abs(phiP - phiQ)
        );

        if (delta >= 0 && delta <= 2) {
            return (Math.min(rP, rQ)*delta + Math.abs(rP - rQ));
        } else {
            return rP + rQ;
        }
    }
}
