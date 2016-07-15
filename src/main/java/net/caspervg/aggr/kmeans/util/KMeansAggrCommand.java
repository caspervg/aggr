package net.caspervg.aggr.kmeans.util;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.caspervg.aggr.core.distance.DistanceMetricChoice;

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
}
