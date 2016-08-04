# Aggr

## Description
This repository contains the Java and Spark components of the Aggr project, as well as a Docker-Compose file which combines all the backend components.

Implemented by Casper Van Gheluwe (UGent) during the summer of 2016, as part of an internship at TenForce.

### Components
#### Aggr-Master
* Polls the triple-store every `X` (currently 10) seconds and look for new aggregation requests.
* If a new aggregation request was added, it will interpret it and forward the request to `Aggr-Worker`. It will also update the status of the request from `not_started` to `in_flight` in the triple-store.
* The workers are started in new threads so that they do not block the polling activity.
* Upon completion of the aggregation, it will update the status of the request to `success` or `failure` (if the worker threw an uncaught exception).
* Located in `net.caspervg.aggr.master`
* Start using the `main` method in `net.caspervg.aggr.master.AggrMasterMain`.
* Expects one command line argument, a HTTP url to the sparql endpoint of the triple-store where it should poll for new aggregation requests.

#### Aggr-Worker
* Started either by the `Spark-Master`, or manually using the CLI in `net.caspervg.aggr.worker.AggrWorkerMain`. The CLI is useful if you do not want to use the Aggregation Request system. The full set of CLI parameters (as of August 4th, 2016) can be consulted below, or alteratively using the `--help` command line argument.

```
Usage: <main class> [options] [command] [command options]
  Options:
  * -d, --dataset-id
       Identifier of the dataset that the aggregations are based on
    --help
       Show this help message
       Default: false
    -i, --input
       Input file (CSV) or SPARQL endpoint to retrieve source data from
       Default: <empty string>
    --input-class
       Package and class name of the class for reading measurements
       Default: net.caspervg.aggr.ext.TimedGeoMeasurement
    -o, --output
       Output directory to store (CSV) results (data) in
       Default: <empty string>
    --output-class
       Package and class name of the class for storing measurements
       Default: net.caspervg.aggr.ext.TimedGeoMeasurement
    -s, --service
       SPARQL endpoint to store results (metadata) in
       Default: <empty string>
    --write-data-csv
       Write data to CSV instead of the triple store (metadata will still go to
       the triple store
       Default: true
    --write-provenance
       Write data on the provenance of centroids, measurements and
       aggregations.Enabling this will greatly increase the time taken to write to the triple store
       Default: false
    -D
       Additional dynamic parameters that could be useful for some aggregation
       command, data reader and/or writer. e.g. 'query', 'latitude_key', ...
       Syntax: -Dkey=value
       Default: {}
  Commands:
    grid      Aggregate the data by rounding to a grid
      Usage: grid [options]
        Options:
          -g, --grid-size
             Rounding to perform on the data to create the grid
             Default: 5.0E-4

    time      Aggregate data based on a time interval
      Usage: time [options]
        Options:
          -d, --max-detail
             Number of time levels to create
             Default: 8

    kmeans      Aggregate the data using a KMeans algorithm
      Usage: kmeans [options]
        Options:
          -n, --iterations
             Number of iterations to do to find the optimal mean locations
             Default: 50
          -m, --metric
             Distance metric to use to calculate distances between data vectors
             Default: EUCLIDEAN
             Possible Values: [EUCLIDEAN, MANHATTAN, CHEBYSHEV, CANBERRA, KARLSRUHE]
          -k, --num-centroids
             Number of centroids (means) to produce
             Default: 10

    combination      Aggregate the data in some way that does not require extra parameters
      Usage: combination [options]

    average      Aggregate the data by taking the average of a certain data point
      Usage: average [options]
        Options:
        * -n, --amount
             Amount of measurements expected for a single point (generally this
             should be #{others}+1)
             Default: 0
          -k, --key
             Key of the measurements to select for the calculation. The
             retrieved value should be convertible to a double, e.g. through
             Double.parseDouble().
             Default: weight
        * -s, --others
             Input files (CSV) with other data to calculate average with

    diff      Aggregate the data by taking the difference between a certain data point and another
      Usage: diff [options]
        Options:
          -k, --key
             Key of the measurements to select for the calculation. The
             retrieved value should be convertible to a double, e.g. through
             Double.parseDouble().
             Default: weight
        * -s, --others
             Input files (CSV) with other data to calculate average with
```

* Reads the measurements from CSV or the triple-store. The interface `worker.read.AggrRead` is responsible for this, implemented by `worker.read.CsvAggrReader` (from CSV) and `worker.read.JenaAggrReader` (using SPARQL queries). How the measurement beans want to populate themselves with the data is left up to them. Classes implementing the Measurement interface have the methods `setData(Map)` and `getReadKeys()` for this purpose.
* Sets up the environment for the aggregation itself, either with or without Spark/Hadoop/HDFS.
* Writes the results of the aggregations to CSV and/or the triple-store. The interfaces `worker.write.AggrResultWriter` and `worker.aggr.AggrWriter` are responsible for this. Users can pick whether they want to write the measurements to CSV (recommended for big data) or to the triple store and whether they want to write provenance information to the triple store.

#### Core
* Contains core interfaces and classes
  * **Child:** indicates that the object can have parents (useful for provenance)
  * **Combinable:** indicates that the object can be combined with other objects (useful for the Combination aggregation)
  * **Dataset:** holds information (uuid and title) about the dataset we are operating in
  * **Measurement:** indicates that the object is a candidate for aggregation. Allows retrieving the data vector (as a `double[]`) and other (meta)data (for writing/aggregations), and also for setting the (meta)data (for reading/aggregations).
  * **UniquelyIdentifiable:** indicates that the object has a unique universal identifier (`UUID`) and `URI`.

#### Aggregation
* Contains the supported aggregations, generally with options for both Spark and normal Java.
  * **Time**
      * Retrieves the maximum and minimum timestamp from the input data
      * Iteratively divides the data into sections (based on start- and end time) with more detail
      * Filters the data so that only measurements within the time slot are kept
      * Outputs a new aggregation with only the filtered measurements and information about the start- and end time of the used time slot, as well as the maximum allowed detail.
      * Spark and plain Java supported
      * Parameters:
          * num_detail: `2^(number of detail levels wanted)` (default `8`)
  * **Grid**
      * Rounds the vector components down so that they will fit into a grid
      * Currently, having a different grid sensitivity for each component is not possible, all components are rounded to the same grid
      * Spark and plain Java supported
      * Parameters:
          * grid_size: sensitivity of the grid (default `0.0005`)
  * **Basic**
      * Combination
          * Loops over the input data set and tries to find measurements that can be somehow combined (e.g. because they have the same vector).
          * The requirements for combination & the logic for the combination itself are set in the `core.bean.Combinable` interface.
          * Only plain Java supported currently
          * Parameters: *nihil*
  * **Difference**
      * Calculates the difference between measurements from input dataset (the `minuends`) and those from the *other* dataset (the `subtrahends`).
      * Only Spark supported currently
      * Uses the method `Combinable#combinationHash` to determine which measurements to subtract
      * Parameters:
         * other: location of a CSV file containing the subtrahends
         * key: key to use to retrieve the component that will be subtracted (default `weight`)
  * **Average**
      * Calculates the average of a certain component from all measurements in the given collection of datasets.
      * uses the method `Combinable#combinationHash` to determine which measurements to subtract
      * Only Spark supported currently
  * **KMeans**
      * Executes a [k-Means](https://en.wikipedia.org/wiki/K-means_clustering) algorithm to determine centroids (means) for the vectors from all measurements in the input dataset.
      * Albeit not yet integrated, several algorithms are provided for the initial seeding, to determine if it's time to end iterating and to select the optimal number of clusters. Information about the specifics behind each algorithm is available in the `Javadoc` of each strategy.
      * Supports both Spark (`SparkKMeansClusterAggregator`, using [Spark.MLLib](http://spark.apache.org/docs/latest/mllib-clustering.html#k-means)) and plain Java. An alternative version (`SparkKMeansAggregator`) using just Spark is also available; it has an unsolved bug/useful feature where it returns less centroids than requested if at any time they became uninhabited.
      * Parameters:
          * num_centroids: number of centroids to find (default `25`)
          * max_iterations: maximum number of iterations to use (default `50`). Implementations may stop earlier if they determine that some other stop condition has been met.
         * metric: distance metric to use to calculate distance between measurements and centroids (default `EUCLIDEAN`). Note that setting this parameters has no effect when using the Spark MLLib k-Means aggregator; as it does not offer this level of customisation.