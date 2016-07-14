package net.caspervg.aggr.core.read;

import java.util.Map;

public abstract class AbstractSparqlAggrReader extends AbstractAggrReader {
    protected static final String DEFAULT_QUERY_FORMAT =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
                    "PREFIX own: <http://www.caspervg.net/test/property#> " +
                    "PREFIX dct: <http://purl.org/dc/terms/> " +
                    "PREFIX mu: <http://mu.semte.ch/vocabularies/core/> " +
                    "SELECT * " +
                    "WHERE { " +
                    "%s" +
                    "?point geo:lat ?latitude . " +
                    "?point geo:lon ?longitude . " +
                    "?point dct:date ?timestamp . " +
                    "OPTIONAL { ?point mu:uuid ?id . } " +
                    "OPTIONAL { ?point dct:source ?source . } " +
                    "MINUS { ?point own:dataset ?d . } " +
                    "}";

    protected static final String DEFAULT_SERVICE = "mem:empty";


    protected String service(Map<String, String> parameters) {
        return parameters.getOrDefault("service", DEFAULT_SERVICE);
    }

    protected String query(Map<String, String> parameters) {
        return parameters.getOrDefault("query",
                String.format(DEFAULT_QUERY_FORMAT, "")
        );
    }

    protected String query(String id, Map<String, String> parameters) {
        return parameters.getOrDefault("query",
                String.format(DEFAULT_QUERY_FORMAT, String.format("?point mu:uuid \"%s\" .", id))
        );
    }
}
