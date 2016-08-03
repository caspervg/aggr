package net.caspervg.aggr.worker.command;

import com.beust.jcommander.Parameter;
import net.caspervg.aggr.master.bean.AggregationRequest;

import java.util.List;

public class DiffAggrCommand {
    @Parameter(
            names = {"-s", "--others"},
            description = "Input files (CSV) with other data to calculate average with",
            required = true,
            variableArity = true
    )
    private List<String> others;

    @Parameter(
            names = {"-k", "--key"},
            description = "Key of the measurements to select for the calculation. " +
                    "The retrieved value should be convertible to a double, e.g. through Double.parseDouble()."
    )
    private String key = "weight";

    public List<String> getOthers() {
        return others;
    }

    public String getKey() {
        return key;
    }

    public static DiffAggrCommand of(AggregationRequest req) {
        DiffAggrCommand command = new DiffAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("avg");

        command.others = req.getParameters().getOthers();
        command.key = req.getParameters().getKey();

        return command;
    }
}
