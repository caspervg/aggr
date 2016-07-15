package net.caspervg.aggr.core.bean;

import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Centroid implements Serializable {

    private String uuid;
    private Double[] vector;
    private Set<Measurement> measurements;

    public Centroid(Double[] vector, Set<Measurement> measurements) {
        this(UUID.randomUUID().toString(), vector, measurements);
    }

    public Centroid(String uuid, Double[] vector, Set<Measurement> measurements) {
        this.uuid = uuid;
        this.vector = vector;
        this.measurements = measurements;
    }

    public Centroid recalculatePosition() {
        if (measurements.size() == 0) return new Centroid(this.vector, new HashSet<>());
        double[] sum = null;

        for (Measurement measurement : this.measurements) {
            Point point = measurement.getPoint();

            if (sum == null) {
                sum = new double[point.getVector().length];
            }

            for (int i = 0; i < sum.length; i++) {
                sum[i] += point.getVector()[i];
            }
        }

        double[] avg = new double[sum.length];
        for (int i = 0; i < avg.length; i++) {
            avg[i] = sum[i] / measurements.size();
        }

        return new Centroid(ArrayUtils.toObject(avg), new HashSet<>(this.measurements));
    }

    public String getUuid() {
        return uuid;
    }

    public Double[] getVector() {
        return vector;
    }

    public Set<Measurement> getMeasurements() {
        return measurements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Centroid)) return false;

        Centroid centroid = (Centroid) o;

        if (uuid != null ? !uuid.equals(centroid.uuid) : centroid.uuid != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(vector, centroid.vector)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(vector);
        return result;
    }

    @Override
    public String toString() {
        return "Centroid{" +
                "uuid='" + uuid + '\'' +
                ", vector=" + Arrays.toString(vector) +
                ", measurements=" + measurements +
                '}';
    }
}
