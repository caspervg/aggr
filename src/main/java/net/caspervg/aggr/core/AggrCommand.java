package net.caspervg.aggr.core;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import net.caspervg.aggr.master.bean.AggregationRequest;
import net.caspervg.aggr.worker.core.bean.Measurement;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AggrCommand {

    private static final String DEFAULT_CLASS = "net.caspervg.aggr.worker.core.bean.impl.TimedGeoMeasurement";
    private static final String DEFAULT_INPUT_CLASS;
    private static final String DEFAULT_OUTPUT_CLASS;
    private String SPARK_URL;
    private String HDFS_URL;

    static {
        DEFAULT_INPUT_CLASS = Optional.ofNullable(System.getenv("INPUT_CLASS")).orElse(DEFAULT_CLASS);
        DEFAULT_OUTPUT_CLASS = Optional.ofNullable(System.getenv("OUTPUT_CLASS")).orElse(DEFAULT_CLASS);
    }

    public AggrCommand() {
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
            "Enabling this will greatly increase the time taken to write to the triple store", arity = 1)
    private boolean writeProvenance = false;

    @Parameter(names = {"-d", "--dataset-id"}, description = "Identifier of the dataset that the aggregations are based " +
            "on", required = true)
    private String datasetId;

    @Parameter(names = {"--input-class"}, description = "Package and class name of the class for reading measurements")
    private String inputClassName = DEFAULT_INPUT_CLASS;

    @Parameter(names = {"--output-class"}, description = "Package and class name of the class for storing measurements")
    private String outputClassName = DEFAULT_OUTPUT_CLASS;

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

    public String getInputClassName() {
        return inputClassName;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Measurement> getInputClass() {
        try {
            return (Class<? extends Measurement>) Class.forName(getInputClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOutputClassName() {
        return outputClassName;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Measurement> getOutputClass() {
        try {
            return (Class<? extends Measurement>) Class.forName(getOutputClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, String> getDynamicParameters() {
        return dynamicParameters;
    }

    public static AggrCommand of(AggregationRequest req) {
        AggrCommand command = new AggrCommand();
        command.input = req.getInput();
        command.output = req.getOutput();
        command.service = req.getService();
        command.writeDataCsv = req.isBigData();
        command.writeProvenance = req.isWriteProvenance();
        command.datasetId = req.getId();
        command.inputClassName = req.getInputClassName();
        command.dynamicParameters = new HashMap<>(req.getParameters().getDynamic());

        command.HDFS_URL = req.getEnvironment().getHdfs();
        command.SPARK_URL = req.getEnvironment().getSpark();

        return command;
    }

}
