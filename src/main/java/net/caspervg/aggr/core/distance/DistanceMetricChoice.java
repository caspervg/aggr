package net.caspervg.aggr.core.distance;

public enum DistanceMetricChoice {
    EUCLIDEAN(new EuclideanDistanceMetric<>()),
    MANHATTAN(new ManhattanDistanceMetric<>()),
    CHEBYSHEV(new ChebyshevDistanceMetric<>()),
    CANBERRA(new CanberraDistanceMetric<>()),
    KARLSRUHE(new KarlsruheDistanceMetric<>());

    private DistanceMetric<Double> metric;

    DistanceMetricChoice(DistanceMetric<Double> metric) {
        this.metric = metric;
    }

    public DistanceMetric<Double> getMetric() {
        return metric;
    }
}
