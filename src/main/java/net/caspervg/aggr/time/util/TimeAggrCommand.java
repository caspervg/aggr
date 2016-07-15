package net.caspervg.aggr.time.util;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Aggregate data based on a time interval")
public class TimeAggrCommand {
    @Parameter(names = {"-d", "--max-detail"}, description = "Number of time levels to create")
    private int maxDetail = 8;

    public int getMaxDetail() {
        return maxDetail;
    }
}
