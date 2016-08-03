package net.caspervg.aggr.worker.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.caspervg.aggr.master.bean.AggregationRequest;

@Parameters(commandDescription = "Aggregate data based on a time interval")
public class TimeAggrCommand {
    @Parameter(names = {"-d", "--max-detail"}, description = "Number of time levels to create")
    private int maxDetail = 8;

    public int getMaxDetail() {
        return maxDetail;
    }

    public static TimeAggrCommand of(AggregationRequest request) {
        TimeAggrCommand command = new TimeAggrCommand();

        command.maxDetail = request.getParameters().getLevels();

        return command;
    }
}
