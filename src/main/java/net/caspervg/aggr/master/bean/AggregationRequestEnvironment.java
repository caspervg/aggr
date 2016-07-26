package net.caspervg.aggr.master.bean;

import org.apache.commons.lang3.StringUtils;

public class AggregationRequestEnvironment {
    private String hdfs;
    private String spark;

    public AggregationRequestEnvironment(String hdfs, String spark) {
        this.hdfs = hdfs;
        this.spark = spark;
    }

    public String getHdfs() {
        return hdfs;
    }

    public String getSpark() {
        return spark;
    }

    public boolean isHdfs() {
        return StringUtils.isNotBlank(getHdfs());
    }

    public boolean isSpark() {
        return StringUtils.isNotBlank(getSpark());
    }

    @Override
    public String toString() {
        return "AggregationRequestEnvironment{" +
                "hdfs='" + hdfs + '\'' +
                ", spark='" + spark + '\'' +
                '}';
    }
}
