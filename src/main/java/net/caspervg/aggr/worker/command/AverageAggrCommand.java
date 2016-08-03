package net.caspervg.aggr.worker.command;

import com.beust.jcommander.Parameter;
import net.caspervg.aggr.master.bean.AggregationRequest;

import java.util.List;

public class AverageAggrCommand {
    @Parameter(
            names = {"-s", "--others"},
            description = "Input files (CSV) with other data to calculate average with",
            required = true,
            variableArity = true
    )
    private List<String> others;

    @Parameter(
            names = {"-n", "--amount"},
            description = "Amount of measurements expected for a single point (generally this should be #{others}+1)",
            required = true
    )
    private long amount;

    @Parameter(
            names = {"-k", "--key"},
            description = "Key of the measurements to select for the calculation. " +
                    "The retrieved value should be convertible to a double, e.g. through Double.parseDouble()."
    )
    private String key = "weight";

    public List<String> getOthers() {
        return others;
    }

    public long getAmount() {
        return amount;
    }

    public String getKey() {
        return key;
    }

    public static AverageAggrCommand of(AggregationRequest req) {
        AverageAggrCommand command = new AverageAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("avg");

        command.others = req.getParameters().getOthers();
        command.amount = req.getParameters().getAmount();
        command.key = req.getParameters().getKey();

        return command;
    }
}
