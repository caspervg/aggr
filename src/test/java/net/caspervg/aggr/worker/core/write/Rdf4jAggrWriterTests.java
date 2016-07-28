package net.caspervg.aggr.worker.core.write;

import com.google.common.collect.Sets;
import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.impl.BasicParent;
import net.caspervg.aggr.worker.core.bean.impl.GeoMeasurement;
import net.caspervg.aggr.worker.core.bean.impl.TimedGeoMeasurement;
import net.caspervg.aggr.worker.core.util.AggrContext;
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

import static net.caspervg.aggr.worker.core.write.AbstractSparqlAggrWriter.*;

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

        //noinspection unchecked
        this.ctx = AggrContext.builder().parameters(Collections.EMPTY_MAP).clazz(TimedGeoMeasurement.class).build();
        this.repository = new SailRepository(new MemoryStore());
        this.writer = new Rdf4jAggrWriter(repository, true);
        this.valueFactory = SimpleValueFactory.getInstance();

        this.geoPoint = valueFactory.createIRI(GEO_PREFIX, "Point");
        this.geoLat = valueFactory.createIRI(OWN_PREFIX, TimedGeoMeasurement.LAT_KEY);
        this.geoLon = valueFactory.createIRI(OWN_PREFIX, TimedGeoMeasurement.LON_KEY);
        this.ownWeight = valueFactory.createIRI(WEIGHT_PROPERTY);
        this.muUUID = valueFactory.createIRI(MU_PREFIX, "uuid");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testWriteMeasurement() {
        Measurement meas = new TimedGeoMeasurement("meas1");
        meas.setTimestamp(time1);
        Map<String, Object> data = new HashMap<>();
        data.put(GeoMeasurement.LAT_KEY, 1.0);
        data.put(GeoMeasurement.LON_KEY, 2.0);
        meas.setParents(Sets.newHashSet(new BasicParent("source1")));
        meas.setData(data);

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

        stmts = conn.getStatements(null, geoLat, valueFactory.createLiteral(1.0D));
        Assert.assertEquals(1, stmts.asList().size());
        stmts = conn.getStatements(null, geoLon, valueFactory.createLiteral(2.0D));
        Assert.assertEquals(1, stmts.asList().size());
        stmts = conn.getStatements(null, DCTERMS.SOURCE, valueFactory.createIRI(MEASUREMENT_URI_PREFIX + "source1"));
        Assert.assertEquals(1, stmts.asList().size());
    }
}
