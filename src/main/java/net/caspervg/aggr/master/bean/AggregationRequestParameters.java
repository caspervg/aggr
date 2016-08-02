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
    private List<String> subtrahends;
    private Map<String, String> dynamic;

    private AggregationRequestParameters(int iterations,
                                         int centroids,
                                         String metric,
                                         int levels,
                                         double gridSize,
                                         List<String> subtrahends,
                                         Map<String, String> dynamic) {
        this.iterations = iterations;
        this.centroids = centroids;
        this.metric = metric;
        this.levels = levels;
        this.gridSize = gridSize;
        this.subtrahends = subtrahends;
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

    public List<String> getSubtrahends() {
        return subtrahends;
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

    public static final class Builder {
        private int iterations;
        private int centroids;
        private String metric;
        private int levels;
        private double gridSize;
        private Map<String, String> dynamic;
        private List<String> subtrahends;

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
            return new AggregationRequestParameters(iterations, centroids, metric, levels, gridSize, subtrahends, dynamic);
        }

        public Builder subtrahendString(String subtrahend) {
            if (Objects.nonNull(subtrahend)) {
                this.subtrahends = Lists.newArrayList(subtrahend.split(","));
            } else {
                this.subtrahends = Lists.newArrayList();
            }
            return this;
        }
    }
}
