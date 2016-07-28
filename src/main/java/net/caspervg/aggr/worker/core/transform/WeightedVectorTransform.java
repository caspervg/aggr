package net.caspervg.aggr.worker.core.transform;

public class WeightedVectorTransform implements Transform<Double[]> {

    private double[] weights;

    public WeightedVectorTransform(double[] weights) {
        this.weights = weights;
    }

    @Override
    public double[] transform(Double[] source) {
        if (source.length != weights.length) {
            throw new IllegalArgumentException("Source length must equal weights length");
        }

        double[] weighted = new double[source.length];

        for (int i = 0; i < weighted.length; i++) {
            weighted[i] = source[i] * this.weights[i];
        }

        return weighted;
    }

}
