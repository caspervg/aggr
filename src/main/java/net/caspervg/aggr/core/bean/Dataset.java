package net.caspervg.aggr.core.bean;

import java.util.UUID;

public class Dataset {

    private String uuid;
    private String title;

    public Dataset(String title) {
        this(UUID.randomUUID().toString(), title);
    }

    public Dataset(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dataset)) return false;

        Dataset dataset = (Dataset) o;

        if (uuid != null ? !uuid.equals(dataset.uuid) : dataset.uuid != null) return false;
        return title != null ? title.equals(dataset.title) : dataset.title == null;

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
