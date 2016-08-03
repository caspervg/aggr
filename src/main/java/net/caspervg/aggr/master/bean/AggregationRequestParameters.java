package net.caspervg.aggr.master.bean;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AggregationRequestParameters {
    private int iterations;
    private int centroids;
    private String metric;
    private int levels;
    private double gridSize;
    private List<String> others;
    private long amount;
    private Map<String, String> dynamic;
    private String key;

    private AggregationRequestParameters(int iterations,
                                         int centroids,
                                         String metric,
                                         int levels,
                                         double gridSize,
                                         List<String> others,
                                         long amount,
                                         String key,
                                         Map<String, String> dynamic) {
        this.iterations = iterations;
        this.centroids = centroids;
        this.metric = metric;
        this.levels = levels;
        this.gridSize = gridSize;
        this.others = others;
        this.amount = amount;
        this.key = key;
        this.dynamic = dynamic;
    }

    public int getIterations() {
        return iterations;
    }

    public int getCentroids() {
        return centroids;
    }

    public String getMetric() {
        return metric;
    }

    public int getLevels() {
        return levels;
    }

    public double getGridSize() {
        return gridSize;
    }

    public List<String> getOthers() {
        return others;
    }

    public Map<String, String> getDynamic() {
        return dynamic;
    }

    @Override
    public String toString() {
        return "AggregationRequestParameters{" +
                "iterations=" + iterations +
                ", centroids=" + centroids +
                ", levels=" + levels +
                ", gridSize=" + gridSize +
                ", dynamic=" + dynamic +
                '}';
    }

    public long getAmount() {
        return amount;
    }

    public String getKey() {
        return key;
    }

    public static final class Builder {
        private int iterations;
        private int centroids;
        private String metric;
        private int levels;
        private double gridSize;
        private Map<String, String> dynamic;
        private List<String> others;
        private long amount;
        private String key;

        private Builder() {
        }

        public static Builder setup() {
            return new Builder();
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder centroids(int centroids) {
            this.centroids = centroids;
            return this;
        }

        public Builder metric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder levels(int levels) {
            this.levels = levels;
            return this;
        }

        public Builder gridSize(double gridSize) {
            this.gridSize = gridSize;
            return this;
        }

        public Builder dynamic(Map<String, String> dynamic) {
            this.dynamic = dynamic;
            return this;
        }

        public AggregationRequestParameters build() {
            return new AggregationRequestParameters(iterations, centroids, metric, levels, gridSize, others, amount, key, dynamic);
        }

        public Builder othersString(String subtrahend) {
            if (Objects.nonNull(subtrahend)) {
                this.others = Lists.newArrayList(subtrahend.split(","));
            } else {
                this.others = Lists.newArrayList();
            }
            return this;
        }

        public Builder amount(long amount) {
            this.amount = amount;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }
    }
}
