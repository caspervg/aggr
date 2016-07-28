package net.caspervg.aggr.master.bean;

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
    private String measurementClassName;

    private AggregationRequest(String id,
                               String input,
                               String output,
                               String aggregationType,
                               boolean writeProvenance,
                               boolean bigData,
                               String measurementClassName,
                               AggregationRequestParameters parameters,
                               AggregationRequestEnvironment environment) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.aggregationType = aggregationType;
        this.writeProvenance = writeProvenance;
        this.bigData = bigData;
        this.measurementClassName = measurementClassName;
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

    public String getMeasurementClassName() {
        return measurementClassName;
    }

    public static final class Builder {
        private String id;
        private String input;
        private String output;
        private String aggregationType;
        private boolean writeProvenance;
        private boolean bigData;
        private String measurementClassName;
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

        public Builder measurementClassName(String measurementClassName) {
            this.measurementClassName = measurementClassName;
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
                    measurementClassName,
                    parameters,
                    environment
            );
        }
    }
}
