package net.caspervg.aggr.core.util;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AggrCommand {

    public static final String SPARK_URL;
    public static final String HDFS_URL;

    static {
        SPARK_URL = Optional.ofNullable(System.getenv("SPARK_MASTER_URL")).orElse("");
        HDFS_URL = Optional.ofNullable(System.getenv("HDFS_URL")).orElse("");
    }

    @Parameter(names = {"-i", "--input"}, description = "Input file (CSV) or SPARQL endpoint to retrieve source data from")
    private String input = "";

    @Parameter(names = {"-o", "--output"}, description = "Output directory to store (CSV) results (data) in")
    private String output = "";

    @Parameter(names = {"-s", "--service"}, description = "SPARQL endpoint to store results (metadata) in")
    private String service = "";

    @Parameter(names = {"--write-data-csv"}, description = "Write data to CSV instead of the triple store (metadata will " +
            "still go to the triple store", arity = 1)
    private boolean writeDataCsv = true;

    @Parameter(names = {"--write-provenance"}, description = "Write data on the provenance of centroids, measurements and aggregations." +
            "Enabling this will greatly increase the time taken to write to the triple store")
    private boolean writeProvenance = false;

    @Parameter(names = {"-d", "--dataset-id"}, description = "Identifier of the dataset that the aggregations are based" +
            "on", required = true)
    private String datasetId;

    @DynamicParameter(names ={"-D"}, description = "Additional dynamic parameters that could be useful for some " +
            "aggregation command, data reader and/or writer. e.g. 'query', 'latitude_key', ...")
    protected Map<String, String> dynamicParameters = new HashMap<>();

    public String getSparkMasterUrl() {
        return SPARK_URL;
    }

    public String getHdfsUrl() {
        return HDFS_URL;
    }

    public boolean isHdfs() {
        return StringUtils.isNotBlank(HDFS_URL);
    }

    public boolean isSpark() {
        return StringUtils.isNotBlank(SPARK_URL);
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getService() {
        return service;
    }

    public boolean isWriteDataCsv() {
        return writeDataCsv;
    }

    public boolean isWriteProvenance() {
        return writeProvenance;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public Map<String, String> getDynamicParameters() {
        return dynamicParameters;
    }

}
