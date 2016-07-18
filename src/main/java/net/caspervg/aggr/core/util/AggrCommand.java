package net.caspervg.aggr.core.util;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AggrCommand {

    @Parameter(names = {"-m", "--master"}, description = "Location of the Spark master. If left blank, plain algorithms will be used instead")
    private String sparkMaster = null;

    @Parameter(names = {"-i", "--input"}, description = "Input file (CSV) or SPARQL endpoint to retrieve source data from")
    private String input = "";

    @Parameter(names = {"-o", "--output"}, description = "Output directory to store (CSV) results (data) in")
    private String output = "";

    @Parameter(names = {"-s", "--service"}, description = "SPARQL endpoint to store results (metadata) in")
    private String service = "";

    @Parameter(names = {"--write-data-csv"}, description = "Write data to CSV instead of the triple store (metadata will " +
            "still go to the triple store", arity = 1)
    private boolean writeDataCsv = true;

    @DynamicParameter(names ={"-D"}, description = "Additional dynamic parameters that could be useful for some " +
            "aggregation command, data reader and/or writer. e.g. 'query', 'latitude_key', ...")
    protected Map<String, String> dynamicParameters = new HashMap<>();

    public String getSparkMaster() {
        return sparkMaster;
    }

    public boolean isSpark() {
        return !StringUtils.isBlank(this.sparkMaster);
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

    public Map<String, String> getDynamicParameters() {
        return dynamicParameters;
    }

}
