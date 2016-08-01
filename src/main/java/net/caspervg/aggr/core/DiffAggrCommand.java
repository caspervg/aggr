package net.caspervg.aggr.core;

import com.beust.jcommander.Parameter;
import net.caspervg.aggr.master.bean.AggregationRequest;

public class DiffAggrCommand {
    @Parameter(names = {"-s", "--subtrahend"}, description = "Input file (CSV) with data to subtract from the input data")
    private String subtrahend = "";

    public String getSubtrahend() {
        return subtrahend;
    }

    public static DiffAggrCommand of(AggregationRequest req) {
        DiffAggrCommand command = new DiffAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("diff");

        command.subtrahend = req.getParameters().getSubtrahend();

        return command;
    }
}
