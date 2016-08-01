package net.caspervg.aggr.exec;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import net.caspervg.aggr.core.*;
import net.caspervg.aggr.worker.basic.BasicAggregationExecution;
import net.caspervg.aggr.worker.core.AggregationExecution;
import net.caspervg.aggr.worker.diff.DiffAggregationExecution;
import net.caspervg.aggr.worker.grid.GridAggregationExecution;
import net.caspervg.aggr.worker.kmeans.KMeansAggregationExecution;
import net.caspervg.aggr.worker.time.TimeAggregationExecution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
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
        DiffAggrCommand dac = new DiffAggrCommand();
        jc.addCommand("diff", dac);

        jc.parse(args);

        if (jc.getParsedCommand() == null) {
            jc.usage();
            return;
        }

        Map<String, AggregationExecution> executionMap = ImmutableMap.of(
                "grid",   new GridAggregationExecution(ac, gac),
                "time",   new TimeAggregationExecution(ac, tac),
                "kmeans", new KMeansAggregationExecution(ac, kac),
                "diff", new DiffAggregationExecution(ac, dac)
        );

        executionMap.getOrDefault(
                jc.getParsedCommand(),
                new BasicAggregationExecution(ac, jc.getParsedCommand())
        ).execute();
    }
}
