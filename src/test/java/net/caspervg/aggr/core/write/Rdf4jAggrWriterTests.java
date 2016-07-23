package net.caspervg.aggr.core.write;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.bean.TimedMeasurement;
import net.caspervg.aggr.core.util.AggrContext;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.caspervg.aggr.core.write.AbstractSparqlAggrWriter.*;

public class Rdf4jAggrWriterTests {

    private AggrContext ctx;
    private Repository repository;
    private Rdf4jAggrWriter writer;
    private ValueFactory valueFactory;
    private IRI geoPoint;
    private IRI geoLat;
    private IRI geoLon;
    private IRI ownWeight;
    private IRI muUUID;
    private LocalDateTime time1 = LocalDateTime.of(2016, 7, 14, 11, 31, 53);
    private LocalDateTime time2 = LocalDateTime.of(2017, 8, 15, 12, 32, 54);

    @Before
    public void initialize() {
        Map<String, String> params = new HashMap<>();

        this.ctx = new AggrContext(Collections.EMPTY_MAP);
        this.repository = new SailRepository(new MemoryStore());
        this.writer = new Rdf4jAggrWriter(repository);
        this.valueFactory = SimpleValueFactory.getInstance();

        this.geoPoint = valueFactory.createIRI(GEO_PREFIX, "Point");
        this.geoLat = valueFactory.createIRI(GEO_PREFIX, "lat");
        this.geoLon = valueFactory.createIRI(GEO_PREFIX, "long");
        this.ownWeight = valueFactory.createIRI(WEIGHT_PROPERTY);
        this.muUUID = valueFactory.createIRI(MU_PREFIX, "uuid");
    }

    @Test
    public void testWriteMeasurement() {
        Measurement meas = TimedMeasurement.Builder
            .setup()
            .withUuid("meas1")
            .withPoint(new Point(new Double[]{1.0, 2.0}))
            .withParent("source1")
            .withTimestamp(time1)
            .build();
        writer.writeMeasurement(meas, ctx);
        IRI measRes = valueFactory.createIRI(MEASUREMENT_URI_PREFIX, "meas1");

        RepositoryConnection conn = repository.getConnection();
        RepositoryResult<Statement> stmts = conn.getStatements(null, null, null, true);

        while(stmts.hasNext()) {
            Statement stmt = stmts.next();
            Assert.assertEquals(measRes, stmt.getSubject());
        }

        stmts = conn.getStatements(null, RDF.TYPE, geoPoint);
        Assert.assertEquals(1, stmts.asList().size());

        stmts = conn.getStatements(null, geoLat, valueFactory.createLiteral("1.0"));
        Assert.assertEquals(1, stmts.asList().size());
        stmts = conn.getStatements(null, geoLon, valueFactory.createLiteral("2.0"));
        Assert.assertEquals(1, stmts.asList().size());
        stmts = conn.getStatements(null, DCTERMS.SOURCE, valueFactory.createIRI(MEASUREMENT_URI_PREFIX + "source1"));
        Assert.assertEquals(1, stmts.asList().size());
    }
}
