/*
package net.caspervg.aggr.core.provenance;

import com.google.common.collect.Lists;
import net.caspervg.aggr.core.bean.UniquelyIdentifiable;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.util.*;

import static net.caspervg.aggr.core.util.Constants.DEFAULT_GRAPH;

public class Rdf4jAggrProvenanceStore implements AggrProvenanceStore {

    private static final int QUERY_PARTITION = 1000;

    private Repository repository;
    private ValueFactory valueFactory;

    public Rdf4jAggrProvenanceStore(Repository repository) {
        this.repository = repository;
        this.valueFactory = SimpleValueFactory.getInstance();
    }

    public Rdf4jAggrProvenanceStore() {
        this(new SailRepository(new MemoryStore()));
    }

    @Override
    public void addParent(UniquelyIdentifiable parent, UniquelyIdentifiable child) {
        Set<Statement> statements = new HashSet<>();

        statements.add(
                valueFactory.createStatement(
                        valueFactory.createIRI(child.getUri()),
                        DCTERMS.SOURCE,
                        valueFactory.createIRI(parent.getUri())
                )
        );

        add(statements);
    }

    @Override
    public void addParents(Iterable<UniquelyIdentifiable> parents, UniquelyIdentifiable child) {
        Set<Statement> statements = new HashSet<>();

        for (UniquelyIdentifiable parent : parents) {
            statements.add(
                    valueFactory.createStatement(
                            valueFactory.createIRI(child.getUri()),
                            DCTERMS.SOURCE,
                            valueFactory.createIRI(parent.getUri())
                    )
            );
        }

        add(statements);
    }

    @Override
    public void addElement(UniquelyIdentifiable collection, UniquelyIdentifiable element) {
        Set<Statement> statements = new HashSet<>();

        statements.add(
                valueFactory.createStatement(
                        valueFactory.createIRI(element.getUri()),
                        DCTERMS.IS_PART_OF,
                        valueFactory.createIRI(collection.getUri())
                )
        );

        add(statements);
    }

    */
/**
     * Adds a collection of {@link Statement} to the SPARQL repository.
     *
     * @param statements Statements to add
     *//*

    private void add(Collection<Statement> statements) {
        List<Statement> statementList = new ArrayList<>(statements);
        List<List<Statement>> statementPartitions = Lists.partition(statementList, QUERY_PARTITION);

        try (RepositoryConnection conn = getConnection()) {
            IRI graphIri = valueFactory.createIRI(DEFAULT_GRAPH);
            for (List<Statement> partition : statementPartitions) {
                conn.add(partition, graphIri);
            }
        }
    }

    private RepositoryConnection getConnection() {
        if (! this.repository.isInitialized()) {
            this.repository.initialize();
        }
        return repository.getConnection();
    }
}
*/
