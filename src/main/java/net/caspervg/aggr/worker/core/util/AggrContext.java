package net.caspervg.aggr.worker.core.util;

import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Map;

public class AggrContext {

    public static final AggrContext EMPTY = new AggrContext(null, null);

    private Map<String, String> parameters;
    private JavaSparkContext sparkContext;
    private FileSystem fileSystem;

    public AggrContext(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public AggrContext(Map<String, String> parameters, JavaSparkContext sparkContext) {
        this.parameters = parameters;
        this.sparkContext = sparkContext;
    }

    public AggrContext(Map<String, String> parameters, JavaSparkContext sparkContext, FileSystem fileSystem) {
        this.parameters = parameters;
        this.sparkContext = sparkContext;
        this.fileSystem = fileSystem;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
