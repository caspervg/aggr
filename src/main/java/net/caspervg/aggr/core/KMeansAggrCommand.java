package net.caspervg.aggr.core;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.caspervg.aggr.master.bean.AggregationRequest;
import net.caspervg.aggr.worker.core.distance.DistanceMetricChoice;
import org.apache.commons.lang3.StringUtils;

@Parameters(commandDescription = "Aggregate the data using a KMeans algorithm")
public class KMeansAggrCommand {
    @Parameter(names = {"-n", "--iterations"}, description = "Number of iterations to do to find the optimal mean locations")
    protected int iterations = 50;

    @Parameter(names = {"-k", "--num-centroids"}, description = "Number of centroids (means) to produce")
    protected int numCentroids = 10;

    @Parameter(names = {"-m", "--metric"}, description = "Distance metric to use to calculate distances between data vectors")
    protected DistanceMetricChoice distanceMetricChoice = DistanceMetricChoice.EUCLIDEAN;

    public int getIterations() {
        return iterations;
    }

    public int getNumCentroids() {
        return numCentroids;
    }

    public DistanceMetricChoice getDistanceMetricChoice() {
        return distanceMetricChoice;
    }
    
    public static KMeansAggrCommand of(AggregationRequest req) {
        KMeansAggrCommand command = new KMeansAggrCommand();

        assert req.getAggregationType().equalsIgnoreCase("kmeans");

        command.iterations = req.getParameters().getIterations();
        command.numCentroids = req.getParameters().getCentroids();
        if (StringUtils.isNotBlank(req.getParameters().getMetric())) {
            command.distanceMetricChoice = DistanceMetricChoice.valueOf(req.getParameters().getMetric().toUpperCase());
        } else {
            command.distanceMetricChoice = DistanceMetricChoice.EUCLIDEAN;
        }

        return command;
    }
}
