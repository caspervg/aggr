package net.caspervg.aggr.worker.core.util;

import net.caspervg.aggr.worker.core.bean.Measurement;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;
import java.util.Map;

public class AggrContext implements Serializable {

    private Map<String, String> parameters;
    private JavaSparkContext sparkContext;
    private FileSystem fileSystem;
    private Class<? extends Measurement> clazz;

    public static Builder builder() {
        return new Builder();
    }

    public AggrContext(Map<String, String> parameters,
                       JavaSparkContext sparkContext,
                       FileSystem fileSystem,
                       Class<? extends Measurement> clazz) {
        this.parameters = parameters;
        this.sparkContext = sparkContext;
        this.fileSystem = fileSystem;
        this.clazz = clazz;
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

    public Class<? extends Measurement> getClazz() {
        return clazz;
    }

    public Measurement newMeasurement() {
        try {
            return this.clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Builder {
        private Map<String, String> parameters;
        private JavaSparkContext sparkContext;
        private FileSystem fileSystem;
        private Class<? extends Measurement> clazz;

        private Builder() {
        }

        public Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder sparkContext(JavaSparkContext sparkContext) {
            this.sparkContext = sparkContext;
            return this;
        }

        public Builder fileSystem(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
            return this;
        }

        public Builder clazz(Class<? extends Measurement> clazz) {
            this.clazz = clazz;
            return this;
        }

        public AggrContext build() {
            return new AggrContext(parameters, sparkContext, fileSystem, clazz);
        }
    }
}
