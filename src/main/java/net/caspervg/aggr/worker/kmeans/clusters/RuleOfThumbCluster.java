package net.caspervg.aggr.worker.kmeans.clusters;

public class RuleOfThumbCluster implements ClusterStrategy {

    private long n;

    public RuleOfThumbCluster(long n) {
        this.n = n;

    }

    @Override
    public int clusters() {
        return (int) Math.ceil(Math.pow(n / (double) 2, 0.5D));
    }
}
