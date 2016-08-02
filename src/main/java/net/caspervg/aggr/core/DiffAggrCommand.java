package net.caspervg.aggr.core;

import com.beust.jcommander.Parameter;
import net.caspervg.aggr.master.bean.AggregationRequest;

import java.util.ArrayList;
import java.util.List;

public class DiffAggrCommand {
    @Parameter(
            names = {"-s", "--subtrahends"},
            description = "Input file (CSV) with data to subtract from the input data",
            required = true,
            variableArity = true
    )
    private List<String> subtrahends;

    public List<String> getSubtrahends() {
        return subtrahends;
    }

    public static DiffAggrCommand of(AggregationRequest req) {
        DiffAggrCommand command = new DiffAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("diff");

        command.subtrahends = req.getParameters().getSubtrahends();

        return command;
    }
}
