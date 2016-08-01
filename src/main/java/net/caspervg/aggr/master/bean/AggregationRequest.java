package net.caspervg.aggr.master.bean;

import org.apache.commons.lang3.StringUtils;

public class AggregationRequest {
    private String id;
    private String input;
    private String output;
    private String aggregationType;
    private boolean writeProvenance;
    private boolean bigData;
    private AggregationRequestParameters parameters;
    private AggregationRequestEnvironment environment;
    private String service;
    private String inputClassName;
    private String outputClassName;

    private AggregationRequest(String id,
                               String input,
                               String output,
                               String aggregationType,
                               boolean writeProvenance,
                               boolean bigData,
                               String inputClassName,
                               String outputClassName,
                               AggregationRequestParameters parameters,
                               AggregationRequestEnvironment environment) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.aggregationType = aggregationType;
        this.writeProvenance = writeProvenance;
        this.bigData = bigData;
        this.inputClassName = inputClassName;
        this.outputClassName = outputClassName;
        this.parameters = parameters;
        this.environment = environment;
    }

    public String getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public boolean isWriteProvenance() {
        return writeProvenance;
    }

    public boolean isBigData() {
        return bigData;
    }

    public AggregationRequestParameters getParameters() {
        return parameters;
    }

    public AggregationRequestEnvironment getEnvironment() {
        return environment;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "AggregationRequest{" +
                "id='" + id + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", aggregationType='" + aggregationType + '\'' +
                ", writeProvenance=" + writeProvenance +
                ", bigData=" + bigData +
                ", parameters=" + parameters +
                ", environment=" + environment +
                '}';
    }

    public String getInputClassName() {
        return inputClassName;
    }

    public String getOutputClassName() {
        return outputClassName;
    }

    public static final class Builder {
        private String id;
        private String input;
        private String output;
        private String aggregationType;
        private boolean writeProvenance;
        private boolean bigData;
        private String inputClassName;
        private String outputClassName;
        private AggregationRequestParameters parameters;
        private AggregationRequestEnvironment environment;

        private Builder() {
        }

        public static Builder setup() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder input(String input) {
            this.input = input;
            return this;
        }

        public Builder output(String output) {
            this.output = output;
            return this;
        }

        public Builder aggregationType(String aggregationType) {
            this.aggregationType = aggregationType;
            return this;
        }

        public Builder writeProvenance(boolean writeProvenance) {
            this.writeProvenance = writeProvenance;
            return this;
        }

        public Builder bigData(boolean bigData) {
            this.bigData = bigData;
            return this;
        }

        public Builder inputClassName(String measurementClassName) {
            if (StringUtils.isNotBlank(measurementClassName)) {
                this.inputClassName = measurementClassName;
            }
            return this;
        }

        public Builder outputClassName(String measurementClassName) {
            if (StringUtils.isNotBlank(measurementClassName)) {
                this.outputClassName = measurementClassName;
            }
            return this;
        }

        public Builder parameters(AggregationRequestParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder environment(AggregationRequestEnvironment environment) {
            this.environment = environment;
            return this;
        }

        public AggregationRequest build() {
            return new AggregationRequest(
                    id,
                    input,
                    output,
                    aggregationType,
                    writeProvenance,
                    bigData,
                    inputClassName,
                    outputClassName,
                    parameters,
                    environment
            );
        }
    }
}
