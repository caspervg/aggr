package net.caspervg.aggr.core.write;

import com.google.common.collect.Iterables;
import net.caspervg.aggr.core.bean.Centroid;
import net.caspervg.aggr.core.bean.Dataset;
import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.aggregation.AbstractAggregation;
import net.caspervg.aggr.core.bean.aggregation.GridAggregation;
import net.caspervg.aggr.core.bean.aggregation.KMeansAggregation;
import net.caspervg.aggr.core.bean.aggregation.TimeAggregation;
import net.caspervg.aggr.core.util.AggrContext;
import net.caspervg.aggr.core.util.untyped.UntypedLiteral;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Implementation of the {@link AggrWriter} interface that writes items to
 * a SPARQL endpoint through RDF4J.
 */
public class Rdf4jAggrWriter extends AbstractSparqlAggrWriter {

    private static final String DEFAULT_SERVICE = "http://localhost:8890/sparql/";

    private Repository repository;
    private ValueFactory valueFactory;
    private IRI geoPoint;
    private IRI ownGridAggr;
    private IRI ownKMeansAggr;
    private IRI ownTimeAggr;
    private IRI geoLat;
    private IRI geoLon;
    private IRI ownWeight;
    private IRI muUUID;

    public Rdf4jAggrWriter(Repository repository) {
        this.repository = repository;
        this.valueFactory = SimpleValueFactory.getInstance();

        this.geoPoint = valueFactory.createIRI(GEO_PREFIX, "Point");
        this.ownGridAggr = valueFactory.createIRI(OWN_CLASS, "GridAggregation");
        this.ownKMeansAggr = valueFactory.createIRI(OWN_CLASS, "KMeansAggregation");
        this.ownTimeAggr = valueFactory.createIRI(OWN_CLASS, "TimeAggregation");
        this.geoLat = valueFactory.createIRI(GEO_PREFIX, "lat");
        this.geoLon = valueFactory.createIRI(GEO_PREFIX, "long");
        this.ownWeight = valueFactory.createIRI(WEIGHT_PROPERTY);
        this.muUUID = valueFactory.createIRI(MU_PREFIX, "uuid");
    }

    public Rdf4jAggrWriter() {
        this(new SailRepository(new MemoryStore()));
    }

    @Override
    public void writeMeasurement(Measurement measurement, AggrContext context) {
        Resource measRes = measurementWithId(measurement.getUuid());

        add(measurementStatements(measurement, measRes));
    }

    @Override
    public void writeMeasurements(Iterable<Measurement> measurements, AggrContext context) {
        Set<Statement> statements = new HashSet<>();

        if (Iterables.isEmpty(measurements)) return;

        for (Measurement measurement : measurements) {
            Resource measRes = measurementWithId(measurement.getUuid());
            statements.addAll(measurementStatements(measurement, measRes));
        }

        add(statements);
    }

    @Override
    public void writeCentroid(Centroid centroid, AggrContext context) {
        Resource centRes = centroidWithId(centroid.getUuid());

        add(centroidStatements(centroid, centRes));
    }

    @Override
    public void writeCentroids(Iterable<Centroid> centroids, AggrContext context) {
        Set<Statement> statements = new HashSet<>();

        if (Iterables.isEmpty(centroids)) return;

        for (Centroid centroid : centroids) {
            Resource centRes = centroidWithId(centroid.getUuid());
            statements.addAll(centroidStatements(centroid, centRes));
        }

        add(statements);
    }

    @Override
    public void writeAggregation(TimeAggregation aggregation, AggrContext context) {
        Set<Statement> statements = new HashSet<>();
        IRI ownStart = valueFactory.createIRI(START_TIME_PROPERTY);
        IRI ownEnd = valueFactory.createIRI(END_TIME_PROPERTY);

        Resource aggRes = aggregationWithId(aggregation.getUuid());

        statements.addAll(aggregationStatements(aggregation, aggRes));

        // Start time of this time aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        ownStart,
                        literalTimestamp(aggregation.getStart())
                )
        );

        // End time of this time aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        ownEnd,
                        literalTimestamp(aggregation.getEnd())
                )
        );

        // Type of the aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        RDF.TYPE,
                        this.ownTimeAggr
                )
        );

        add(statements);
    }

    @Override
    public void writeAggregation(KMeansAggregation aggregation, AggrContext context) {
        Set<Statement> statements = new HashSet<>();
        IRI ownIterations = valueFactory.createIRI(ITERATIONS_PROPERTY);
        IRI ownNumCentroids = valueFactory.createIRI(NUM_CENTROIDS_PROPERTY);

        Resource aggRes = aggregationWithId(aggregation.getUuid());

        statements.addAll(aggregationStatements(aggregation, aggRes));

        // Number of iterations of the KMeans aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        ownIterations,
                        valueFactory.createLiteral(BigInteger.valueOf(aggregation.getN()))
                )
        );

        // Number of means of the KMeans aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        ownNumCentroids,
                        valueFactory.createLiteral(BigInteger.valueOf(aggregation.getK()))
                )
        );

        // Type of the aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        RDF.TYPE,
                        this.ownKMeansAggr
                )
        );

        add(statements);
    }

    @Override
    public void writeAggregation(GridAggregation aggregation, AggrContext context) {
        Set<Statement> statements = new HashSet<>();
        IRI ownGridSize = valueFactory.createIRI(GRID_SIZE_PROPERTY);

        Resource aggRes = aggregationWithId(aggregation.getUuid());

        statements.addAll(aggregationStatements(aggregation, aggRes));

        // Grid size of the grid aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        ownGridSize,
                        valueFactory.createLiteral(aggregation.getGridSize())
                )
        );

        // Type of the aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        RDF.TYPE,
                        this.ownGridAggr
                )
        );

        add(statements);
    }

    protected Collection<Statement> measurementStatements(Measurement measurement, Resource measRes) {
        Set<Statement> statements = new HashSet<>();

        double latitude = measurement.getPoint().getVector()[0];
        double longitude = measurement.getPoint().getVector()[1];
        LocalDateTime timestamp = measurement.getTimestamp();
        String id = measurement.getUuid();
        Optional<String> parent = measurement.getParent();

        // Type of the measurement
        statements.add(
                valueFactory.createStatement(
                        measRes,
                        RDF.TYPE,
                        this.geoPoint
                )
        );

        // mu-UUID of the measurement
        statements.add(
                valueFactory.createStatement(
                        measRes,
                        this.muUUID,
                        stringLiteral(id)
                )
        );

        // Latitude of the measurement
        statements.add(
                valueFactory.createStatement(
                        measRes,
                        this.geoLat,
                        stringLiteral(String.valueOf(latitude))    // Spec requires string for latitude
                )
        );

        // Longitude of the measurement
        statements.add(
                valueFactory.createStatement(
                        measRes,
                        this.geoLon,
                        stringLiteral(String.valueOf(longitude))    // Spec requires string for longitude
                )
        );

        // Timestamp of the measurement
        statements.add(
                valueFactory.createStatement(
                        measRes,
                        DCTERMS.DATE,
                        literalTimestamp(timestamp)
                )
        );

        // Link to the parent measurement, if present
        if (parent.isPresent()) {
            statements.add(
                    valueFactory.createStatement(
                            measRes,
                            DCTERMS.SOURCE,
                            measurementWithId(parent.get())
                    )
            );
        }

        return statements;
    }

    protected Collection<Statement> centroidStatements(Centroid centroid, Resource centRes) {
        Set<Statement> statements = new HashSet<>();

        double latitude = centroid.getVector()[0];
        double longitude = centroid.getVector()[1];
        int weight = centroid.getMeasurements().size();

        // Type of the centroid
        statements.add(
                valueFactory.createStatement(
                        centRes,
                        RDF.TYPE,
                        this.geoPoint
                )
        );

        // Latitude of the centroid
        statements.add(
                valueFactory.createStatement(
                        centRes,
                        this.geoLat,
                        stringLiteral(String.valueOf(latitude))     // Spec requires string for latitude
                )
        );

        // Longitude of the centroid
        statements.add(
                valueFactory.createStatement(
                        centRes,
                        this.geoLon,
                        stringLiteral(String.valueOf(longitude))    // Spec requires string for longitude
                )
        );

        // Weight of the centroid
        statements.add(
                valueFactory.createStatement(
                        centRes,
                        this.ownWeight,
                        valueFactory.createLiteral(BigInteger.valueOf(weight))
                )
        );

        // mu-UUID of the centroid
        statements.add(
                valueFactory.createStatement(
                        centRes,
                        this.muUUID,
                        stringLiteral(centroid.getUuid())
                )
        );

        // Source measurements of the centroids
        for (Measurement measurement : centroid.getMeasurements()) {
            statements.add(
                    valueFactory.createStatement(
                            centRes,
                            DCTERMS.SOURCE,
                            measurementWithId(measurement.getUuid())
                    )
            );
        }

        return statements;
    }

    protected Collection<Statement> aggregationStatements(AbstractAggregation aggregation, Resource aggRes) {
        Set<Statement> statements = new HashSet<>();

        // mu-UUID of the aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        muUUID,
                        stringLiteral(aggregation.getUuid())
                )
        );

        // Dataset of the aggregation
        statements.add(
                valueFactory.createStatement(
                        aggRes,
                        DCTERMS.IS_PART_OF,
                        datasetWithId(aggregation.getDataset().getUuid())
                )
        );

        // Sources of the aggregation
        for (Measurement measurement : aggregation.getSources()) {
            statements.add(
                    valueFactory.createStatement(
                            aggRes,
                            DCTERMS.SOURCE,
                            measurementWithId(measurement.getUuid())
                    )
            );

            statements.add(
                    valueFactory.createStatement(
                            measurementWithId(measurement.getUuid()),
                            DCTERMS.IS_PART_OF,
                            aggRes
                    )
            );
        }

        return statements;
    }

    @Override
    public void writeDataset(Dataset dataset, AggrContext context) {
        Set<Statement> statements = new HashSet<>();

        Resource dsRes = datasetWithId(dataset.getUuid());

        // mu-UUID of the dataset
        statements.add(
                valueFactory.createStatement(
                        dsRes,
                        muUUID,
                        stringLiteral(dataset.getUuid())
                )
        );

        // Title of the dataset
        statements.add(
                valueFactory.createStatement(
                        dsRes,
                        DCTERMS.TITLE,
                        stringLiteral(dataset.getTitle())
                )
        );

        add(statements);
    }

    /**
     * Lazily creates or retrieves a Dataset resource with given id
     *
     * @param id id of the dataset
     * @return RDF4J resource for the dataset
     */
    private Resource datasetWithId(String id) {
        return valueFactory.createIRI(DATASET_URI_PREFIX, id);
    }

    /**
     * Lazily creates or retrieves an Aggregation resource with given id
     *
     * @param id id of the aggregation
     * @return RDF4J resource for the aggregation
     */
    private Resource aggregationWithId(String id) {
        return valueFactory.createIRI(AGGREGATION_URI_PREFIX, id);
    }

    /**
     * Lazily creates or retrieves a Measurement resource with given id
     *
     * @param id id of the measurement
     * @return RDF4J resource for the measurement
     */
    private Resource measurementWithId(String id) {
        return valueFactory.createIRI(MEASUREMENT_URI_PREFIX, id);
    }

    /**
     * Lazily creates or retrieves a Centroid resource with given id
     *
     * @param id id of the centroid
     * @return RDF4J resource for the centroid
     */
    private Resource centroidWithId(String id) {
        return valueFactory.createIRI(CENTROID_URI_PREFIX, id);
    }

    /**
     * Creates a {@link UntypedLiteral} that wraps the content, as a workaround for RDF4J's strong typing
     * of all strings as <code>xsd:string</code>.
     *
     * @param content Content to wrap
     * @return Wrapped untyped literal
     */
    private Value stringLiteral(String content) {
        return new UntypedLiteral(content);
    }

    /**
     * Converts a {@link LocalDateTime} instance to a {@link Literal}.
     *
     * @param dateTime Date time to convert
     * @return Converted literal
     */
    private Literal literalTimestamp(LocalDateTime dateTime) {
        return valueFactory.createLiteral(
                Date.from(
                        dateTime.toInstant(
                                ZoneOffset.UTC
                        )
                )
        );
    }

    /**
     * Adds a collection of {@link Statement} to the SPARQL repository.
     *
     * @param statements Statements to add
     */
    private void add(Collection<Statement> statements) {
        try (RepositoryConnection conn = getConnection()) {
            conn.add(statements, valueFactory.createIRI(DEFAULT_GRAPH));
        }
    }

    private RepositoryConnection getConnection() {
        if (! this.repository.isInitialized()) {
            this.repository.initialize();
        }
        return repository.getConnection();
    }
}
