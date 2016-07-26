package net.caspervg.aggr.master.bean;

import net.caspervg.aggr.worker.core.util.untyped.UntypedLiteral;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.caspervg.aggr.worker.core.util.Constants.DEFAULT_GRAPH;

public class Rdf4jAggrRequestUpdater {

    protected static final String OWN_PREFIX = "http://www.caspervg.net/test/";
    protected static final String MU_PREFIX = "http://mu.semte.ch/vocabularies/core/";
    protected static final String OWN_PROPERTY = OWN_PREFIX + "property#";
    protected static final String STATUS_PROPERTY = OWN_PROPERTY + "status";
    protected static final String REQUEST_URI_PREFIX = OWN_PREFIX + "aggregation-request/";
    protected static final String[] POSSIBLE_STATUSES = new String[] {"in_flight", "not_started", "success", "failure"};

    private Repository repository;
    private ValueFactory valueFactory;
    private IRI ownStatus;

    public Rdf4jAggrRequestUpdater(Repository repository) {
        this.repository = repository;
        this.valueFactory = SimpleValueFactory.getInstance();
        this.ownStatus = valueFactory.createIRI(STATUS_PROPERTY);
    }

    public void updateStatus(String id, String newStatus) {
        Set<Statement> addStatements = new HashSet<>();
        Set<Statement> rmStatements = new HashSet<>();

        Resource reqRes = requestWithId(id);

        for (String possibleStatus : POSSIBLE_STATUSES) {
            rmStatements.add(
                    valueFactory.createStatement(
                            reqRes,
                            this.ownStatus,
                            new UntypedLiteral(possibleStatus)
                    )
            );
        }

        addStatements.add(
                valueFactory.createStatement(
                        reqRes,
                        this.ownStatus,
                        new UntypedLiteral(newStatus)
                )
        );

        rm(rmStatements);
        add(addStatements);
    }

    private Resource requestWithId(String id) {
        return valueFactory.createIRI(REQUEST_URI_PREFIX, id);
    }

    private void rm(Collection<Statement> statements) {
        try (RepositoryConnection conn = getConnection()) {
            IRI graphIri = valueFactory.createIRI(DEFAULT_GRAPH);
            conn.remove(statements, graphIri);
        }
    }

    private void add(Collection<Statement> statements) {
        try (RepositoryConnection conn = getConnection()) {
            IRI graphIri = valueFactory.createIRI(DEFAULT_GRAPH);
            conn.add(statements, graphIri);
        }
    }

    private RepositoryConnection getConnection() {
        if (! this.repository.isInitialized()) {
            this.repository.initialize();
        }
        return repository.getConnection();
    }
}
