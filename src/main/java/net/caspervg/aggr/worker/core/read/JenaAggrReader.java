package net.caspervg.aggr.worker.core.read;

import net.caspervg.aggr.worker.core.bean.Measurement;
import net.caspervg.aggr.worker.core.bean.UniquelyIdentifiable;
import net.caspervg.aggr.worker.core.bean.impl.BasicParent;
import net.caspervg.aggr.worker.core.util.AggrContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.jdbc.mem.MemDriver;
import org.apache.jena.jdbc.remote.RemoteEndpointDriver;

import java.sql.*;
import java.util.*;

/**
 * Implementation of the {@link AggrReader} interface that reads measurements through Jena, through either
 * a JDBC connection to a remote Virtuoso instance, or an in-memory ontology (e.g. from a .ttl file)
 */
public class JenaAggrReader extends AbstractSparqlAggrReader {

    @Override
    public Optional<Measurement> read(String id, AggrContext context) {
        Map<String, String> parameters = context.getParameters();
        String query = this.query(id, parameters);

        try (Connection conn = getConnection(parameters)) {

            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return Optional.of(
                            measurementFromRecord(context, rs)
                    );
                } else {
                    return Optional.empty();
                }
            } catch (SQLException ex) {
                System.err.println(query);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Measurement> read(AggrContext context) {
        Set<Measurement> measurements = new HashSet<>();

        Map<String, String> parameters = context.getParameters();
        String query = this.query(parameters);

        try (Connection conn = getConnection(parameters)) {

            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    measurements.add(measurementFromRecord(context, rs));
                }
            } catch (SQLException ex) {
                System.err.println(query);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return measurements;
    }

    private Measurement measurementFromRecord(AggrContext context, ResultSet record) throws SQLException {
        Map<String, String> params = context.getParameters();
        Measurement measurement = context.newInputMeasurement();

        String idKey = idKey(params);
        String srcKey = sourceKey(params);

        String measId = record.getString(idKey);
        if (StringUtils.isNotBlank(measId)) {
            measurement.setUuid(measId);
        }

        String parentId = record.getString(srcKey);
        if (StringUtils.isNotBlank(parentId)) {
            Set<UniquelyIdentifiable> parents = new HashSet<>();
            parents.add(new BasicParent(parentId));
            measurement.setParents(parents);
        }

        Map<String, Object> data = new HashMap<>();
        for (String key : measurement.getReadKeys()) {
            data.put(key, record.getObject(key));
        }
        measurement.setData(data);

        return measurement;

    }

    private Connection getConnection(Map<String, String> parameters) throws SQLException {
        String service = this.service(parameters);

        if (service(parameters).startsWith("mem:")) {
            MemDriver.register();

            return DriverManager.getConnection("jdbc:jena:" + service);
        } else {
            RemoteEndpointDriver.register();
            return  DriverManager.getConnection("jdbc:jena:remote:query=" + service);
        }
    }
}
