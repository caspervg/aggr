package net.caspervg.aggr.exec;

import com.beust.jcommander.JCommander;
import net.caspervg.aggr.core.AggregationExecution;
import net.caspervg.aggr.core.util.AggrCommand;
import net.caspervg.aggr.grid.GridAggregationExecution;
import net.caspervg.aggr.grid.util.GridAggrCommand;
import net.caspervg.aggr.kmeans.KMeansAggregationExecution;
import net.caspervg.aggr.kmeans.util.KMeansAggrCommand;
import net.caspervg.aggr.time.TimeAggregationExecution;
import net.caspervg.aggr.time.util.TimeAggrCommand;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AggrMain {
    public static void main(String[] args) throws IOException, URISyntaxException {
        AggrCommand ac = new AggrCommand();
        JCommander jc = new JCommander(ac);

        GridAggrCommand gac = new GridAggrCommand();
        jc.addCommand("grid", gac);
        TimeAggrCommand tac = new TimeAggrCommand();
        jc.addCommand("time", tac);
        KMeansAggrCommand kac = new KMeansAggrCommand();
        jc.addCommand("kmeans", kac);

        jc.parse(args);

        if (jc.getParsedCommand() == null) {
            jc.usage();
            return;
        }

        Map<String, AggregationExecution> executionMap = new HashMap<>();
        executionMap.put("grid", new GridAggregationExecution(ac, gac));
        executionMap.put("time", new TimeAggregationExecution(ac, tac));
        executionMap.put("kmeans", new KMeansAggregationExecution(ac, kac));

        executionMap.getOrDefault(jc.getParsedCommand(), jc::usage).execute();
    }
}
