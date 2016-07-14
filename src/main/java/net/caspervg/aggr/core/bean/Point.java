package net.caspervg.aggr.core.bean;

import java.io.Serializable;
import java.util.Arrays;

public class Point implements Serializable, Cloneable {
    private Double[] vector;

    public Point(Double[] vector) {
        this.vector = vector;
    }

    public Double[] getVector() {
        return vector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(vector, point.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override
    public String toString() {
        return "Point{" +
                "vector=" + Arrays.toString(vector) +
                '}';
    }

    @Override
    public Object clone() {
        return new Point(Arrays.stream(vector).toArray(Double[]::new));
    }
}
