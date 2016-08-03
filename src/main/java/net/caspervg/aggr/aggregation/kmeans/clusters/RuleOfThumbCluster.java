package net.caspervg.aggr.aggregation.kmeans.clusters;

public class RuleOfThumbCluster implements ClusterStrategy {

    private long n;

    public RuleOfThumbCluster(long n) {
        this.n = n;

    }

    /**
     * Returns the suggested number of clusters using the well-known rule of thumb: {@code k = (n/2)^0.5}
     * @return {@inheritDoc}
     */
    @Override
    public int clusters() {
        return (int) Math.ceil(Math.pow(n / (double) 2, 0.5D));
    }
}
