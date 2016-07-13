package net.caspervg.aggr.core.read;

import net.caspervg.aggr.core.bean.Measurement;
import net.caspervg.aggr.core.bean.Point;
import net.caspervg.aggr.core.util.AggrContext;
import org.apache.jena.jdbc.mem.MemDriver;
import org.apache.jena.jdbc.remote.RemoteEndpointDriver;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JenaAggrReader extends AbstractSparqlAggrReader {

    @Override
    public Optional<Measurement> read(String id, AggrContext context) {
        Map<String, String> parameters = context.getParameters();
        String query = this.query(id, parameters);
        String latitudeKey = this.latitudeKey(parameters);
        String longitudeKey = this.longitudeKey(parameters);
        String timeKey = this.timestampKey(parameters);
        String idKey = this.idKey(parameters);
        String sourceKey = this.sourceKey(parameters);

        try (Connection conn = getConnection(parameters)) {

            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    Double[] vector = new Double[]{
                            Double.parseDouble(rs.getString(rs.findColumn(latitudeKey))),
                            Double.parseDouble(rs.getString(rs.findColumn(longitudeKey)))
                    };
                    LocalDateTime time = LocalDateTime.parse(
                            rs.getString(
                                    rs.findColumn(timeKey)
                            ),
                            DateTimeFormatter.ISO_DATE_TIME
                    );
                    String foundId = rs.getString(rs.findColumn(idKey));
                    URL source = rs.getURL(rs.findColumn(sourceKey));

                    return Optional.of(new Measurement(foundId, new Point(vector), source.toString(), time));
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
        String latitudeKey = this.latitudeKey(parameters);
        String longitudeKey = this.longitudeKey(parameters);
        String timeKey = this.timestampKey(parameters);
        String idKey = this.idKey(parameters);
        String sourceKey = this.sourceKey(parameters);

        try (Connection conn = getConnection(parameters)) {

            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Double[] vector = new Double[]{
                            Double.parseDouble(rs.getString(rs.findColumn(latitudeKey))),
                            Double.parseDouble(rs.getString(rs.findColumn(longitudeKey)))
                    };
                    LocalDateTime time = LocalDateTime.parse(
                            rs.getString(
                                    rs.findColumn(timeKey)
                            ),
                            DateTimeFormatter.ISO_DATE_TIME
                    );
                    String id = rs.getString(rs.findColumn(idKey));
                    URL source = rs.getURL(rs.findColumn(sourceKey));

                    measurements.add(new Measurement(id, new Point(vector), source.toString(), time));
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
