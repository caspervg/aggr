package net.caspervg.aggr.core.provenance;

import net.caspervg.aggr.core.bean.UniquelyIdentifiable;

public interface AggrProvenanceStore {
    void addParent(UniquelyIdentifiable parent, UniquelyIdentifiable child);
    void addParents(Iterable<UniquelyIdentifiable> parents, UniquelyIdentifiable child);
    void addElement(UniquelyIdentifiable collection, UniquelyIdentifiable element);
}
