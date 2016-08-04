package net.caspervg.aggr.worker.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.caspervg.aggr.master.bean.AggregationRequest;

import java.util.List;

@Parameters(commandDescription = "Aggregate the data by taking the difference between a certain data point and another")
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

        assert req.getAggregationType().equalsIgnoreCase("diff");

        command.others = req.getParameters().getOthers();
        command.key = req.getParameters().getKey();

        return command;
    }
}
