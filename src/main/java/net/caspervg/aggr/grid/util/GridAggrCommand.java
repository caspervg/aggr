package net.caspervg.aggr.grid.util;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Aggregate the data by rounding to a grid")
public class GridAggrCommand {

    @Parameter(names = {"-g", "--grid-size"}, description = "Rounding to perform on the data to create the grid")
    private double gridSize = 0.0005;

    public double getGridSize() {
        return gridSize;
    }
}
