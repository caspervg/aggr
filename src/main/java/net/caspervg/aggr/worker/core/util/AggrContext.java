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
    private Class<? extends Measurement> inputClass;
    private Class<? extends Measurement> outputClass;

    public static Builder builder() {
        return new Builder();
    }

    public AggrContext(Map<String, String> parameters,
                       JavaSparkContext sparkContext,
                       FileSystem fileSystem,
                       Class<? extends Measurement> inputClass,
                       Class<? extends Measurement> outputClass) {
        this.parameters = parameters;
        this.sparkContext = sparkContext;
        this.fileSystem = fileSystem;
        this.inputClass = inputClass;
        this.outputClass = outputClass;
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

    public Class<? extends Measurement> getInputClass() {
        return inputClass;
    }

    public Class<? extends Measurement> getOutputClass() {
        return outputClass;
    }

    public Measurement newInputMeasurement() {
        try {
            return this.inputClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Measurement newOutputMeasurement() {
        try {
            return this.outputClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Builder {
        private Map<String, String> parameters;
        private JavaSparkContext sparkContext;
        private FileSystem fileSystem;
        private Class<? extends Measurement> inputClass;
        private Class<? extends Measurement> outputClass;

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

        public Builder inputClass(Class<? extends Measurement> clazz) {
            this.inputClass = clazz;
            return this;
        }

        public Builder outputClass(Class<? extends Measurement> clazz) {
            this.outputClass = clazz;
            return this;
        }

        public AggrContext build() {
            return new AggrContext(parameters, sparkContext, fileSystem, inputClass, outputClass);
        }
    }
}
