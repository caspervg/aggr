package net.caspervg.aggr.core.util;

import java.util.Map;

public class AggrContext {
    private Map<String, String> parameters;

    public AggrContext(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
