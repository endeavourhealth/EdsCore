package org.endeavourhealth.core.database.rdbms.logback.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class RdbmsLoggingEventPropertyPK implements Serializable {
    private long eventId;
    private String mappedKey;

    @Column(name = "event_id")
    @Id
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Column(name = "mapped_key")
    @Id
    public String getMappedKey() {
        return mappedKey;
    }

    public void setMappedKey(String mappedKey) {
        this.mappedKey = mappedKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RdbmsLoggingEventPropertyPK that = (RdbmsLoggingEventPropertyPK)o;

        if (eventId != that.eventId) return false;
        if (mappedKey != null ? !mappedKey.equals(that.mappedKey) : that.mappedKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (mappedKey != null ? mappedKey.hashCode() : 0);
        return result;
    }
}

