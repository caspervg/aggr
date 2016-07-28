package net.caspervg.aggr.worker.core.bean;

import java.util.Set;

public interface Parental {
    Set<UniquelyIdentifiable> getParents();
    void setParents(Set<UniquelyIdentifiable> parents);
}
