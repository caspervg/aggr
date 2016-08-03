package net.caspervg.aggr.worker;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import net.caspervg.aggr.aggregation.average.AverageAggregationExecution;
import net.caspervg.aggr.aggregation.basic.BasicAggregationExecution;
import net.caspervg.aggr.worker.command.*;
import net.caspervg.aggr.aggregation.AggregationExecution;
import net.caspervg.aggr.aggregation.grid.GridAggregationExecution;
import net.caspervg.aggr.aggregation.kmeans.KMeansAggregationExecution;
import net.caspervg.aggr.aggregation.time.TimeAggregationExecution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class AggrWorkerMain {
    public static void main(String[] args) throws IOException, URISyntaxException {
        AggrCommand ac = new AggrCommand();
        JCommander jc = new JCommander(ac);

        GridAggrCommand gac = new GridAggrCommand();
        jc.addCommand("grid", gac);
        TimeAggrCommand tac = new TimeAggrCommand();
        jc.addCommand("time", tac);
        KMeansAggrCommand kac = new KMeansAggrCommand();
        jc.addCommand("kmeans", kac);
        BasicCommand bac = new BasicCommand();
        jc.addCommand("combination", bac);
        AverageAggrCommand dac = new AverageAggrCommand();
        jc.addCommand("average", dac);

        jc.parse(args);

        if (jc.getParsedCommand() == null) {
            jc.usage();
            return;
        }

        Map<String, AggregationExecution> executionMap = ImmutableMap.of(
                "grid",   new GridAggregationExecution(ac, gac),
                "time",   new TimeAggregationExecution(ac, tac),
                "kmeans", new KMeansAggregationExecution(ac, kac),
                "average", new AverageAggregationExecution(ac, dac)
        );

        executionMap.getOrDefault(
                jc.getParsedCommand(),
                new BasicAggregationExecution(ac, jc.getParsedCommand())
        ).execute();
    }
}
