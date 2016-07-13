package net.caspervg.aggr.core.write;

public abstract class AbstractSparqlAggrWriter implements AggrWriter {

    protected static final String GEO_PREFIX = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    protected static final String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    protected static final String OWN_PREFIX = "http://www.caspervg.net/test/";
    protected static final String DCT_PREFIX = "http://purl.org/dc/terms/";
    protected static final String MU_PREFIX = "http://mu.semte.ch/vocabularies/core/";
    protected static final String OWN_PROPERTY = OWN_PREFIX + "property#";
    protected static final String OWN_CLASS = OWN_PREFIX + "class#";
    protected static final String DATASET_PROPERTY = OWN_PROPERTY + "dataset";
    protected static final String WEIGHT_PROPERTY = OWN_PROPERTY + "weight";
    protected static final String START_TIME_PROPERTY = OWN_PROPERTY + "start";
    protected static final String END_TIME_PROPERTY = OWN_PROPERTY + "end";
    protected static final String GRID_SIZE_PROPERTY = OWN_PROPERTY + "grid_size";
    protected static final String ITERATIONS_PROPERTY = OWN_PROPERTY + "iterations";
    protected static final String NUM_CENTROIDS_PROPERTY = OWN_PROPERTY + "num_centroids";
    protected static final String DATASET_URI_PREFIX = OWN_PREFIX + "datasets/";
    protected static final String CENTROID_URI_PREFIX = OWN_PREFIX + "centroids/";
    protected static final String MEASUREMENT_URI_PREFIX = OWN_PREFIX + "measurements/";
    protected static final String AGGREGATION_URI_PREFIX = OWN_PREFIX + "aggregations/";
    protected static final String DEFAULT_GRAPH = "http://mu.semte.ch/application";

}
