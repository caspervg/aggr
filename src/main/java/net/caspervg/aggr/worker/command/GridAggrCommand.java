package net.caspervg.aggr.worker.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.caspervg.aggr.master.bean.AggregationRequest;

@Parameters(commandDescription = "Aggregate the data by rounding to a grid")
public class GridAggrCommand {

    @Parameter(names = {"-g", "--grid-size"}, description = "Rounding to perform on the data to create the grid")
    private double gridSize = 0.0005;

    public double getGridSize() {
        return gridSize;
    }

    public static GridAggrCommand of(AggregationRequest req) {
        GridAggrCommand command = new GridAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("grid");

        command.gridSize = req.getParameters().getGridSize();

        return command;
    }
}
