package net.caspervg.aggr.exec;

import net.caspervg.aggr.core.*;
import net.caspervg.aggr.master.JenaAggrRequestReader;
import net.caspervg.aggr.master.bean.AggregationRequest;
import net.caspervg.aggr.master.bean.Rdf4jAggrRequestUpdater;
import net.caspervg.aggr.worker.average.AverageAggregationExecution;
import net.caspervg.aggr.worker.basic.BasicAggregationExecution;
import net.caspervg.aggr.worker.core.util.untyped.UntypedSPARQLRepository;
import net.caspervg.aggr.worker.diff.DiffAggregationExecution;
import net.caspervg.aggr.worker.grid.GridAggregationExecution;
import net.caspervg.aggr.worker.kmeans.KMeansAggregationExecution;
import net.caspervg.aggr.worker.time.TimeAggregationExecution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class AggrMasterMain {
    public static void main(String[] args) throws SQLException, InterruptedException, IOException, URISyntaxException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Need the URL to a sparql service");
        }

        JenaAggrRequestReader reader = new JenaAggrRequestReader(args[0]);
        Rdf4jAggrRequestUpdater updater = new Rdf4jAggrRequestUpdater(new UntypedSPARQLRepository(args[0]));

        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("Looking for new requests");
            Iterable<AggregationRequest> newRequests = reader.read();
            for (AggregationRequest request : newRequests) {
                request.setService(args[0]);

                System.out.println("Found new request with id " + request.getId() + " and type " + request.getAggregationType());
                updater.updateStatus(request.getId(), "in_flight");

                AggrCommand mainCommand = AggrCommand.of(request);
                switch (request.getAggregationType().toLowerCase()) {
                    case "kmeans":
                        KMeansAggrCommand kmeansCommand = KMeansAggrCommand.of(request);
                        new Thread(() -> {
                            try {
                                new KMeansAggregationExecution(mainCommand, kmeansCommand).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e) {
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                        break;
                    case "grid":
                        GridAggrCommand gridCommand = GridAggrCommand.of(request);
                        new Thread(() -> {
                            try {
                                new GridAggregationExecution(mainCommand, gridCommand).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e) {
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                        break;
                    case "time":
                        TimeAggrCommand timeCommand = TimeAggrCommand.of(request);
                        new Thread(() -> {
                            try {
                                new TimeAggregationExecution(mainCommand, timeCommand).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e) {
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                        break;
                    case "average":
                        AverageAggrCommand avgCommand = AverageAggrCommand.of(request);
                        new Thread(() -> {
                            try {
                                new AverageAggregationExecution(mainCommand, avgCommand).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e){
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                        break;
                    case "diff":
                        DiffAggrCommand diffCommand = DiffAggrCommand.of(request);
                        new Thread(() -> {
                            try {
                                new DiffAggregationExecution(mainCommand, diffCommand).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e){
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                        break;
                    default:
                        new Thread(() -> {
                            try {
                                new BasicAggregationExecution(mainCommand, request.getAggregationType().toLowerCase()).execute();
                                updater.updateStatus(request.getId(), "success");
                            } catch (Exception e) {
                                e.printStackTrace();
                                updater.updateStatus(request.getId(), "failure");
                            }
                        }).start();
                }
            }
            System.out.println("Finished processing new requests");

            Thread.sleep(10000);
        }
    }
}
