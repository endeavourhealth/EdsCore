package org.endeavourhealth.core.database.rdbms.logback.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "logging_event_property", schema = "public", catalog = "logback")
@IdClass(RdbmsLoggingEventPropertyPK.class)
public class RdbmsLoggingEventProperty implements Serializable {

    private long eventId;
    private String mappedKey;
    private String mappedValue;

    @Id
    @Column(name = "event_id")
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Id
    @Column(name = "mapped_key")
    public String getMappedKey() {
        return mappedKey;
    }

    public void setMappedKey(String mappedKey) {
        this.mappedKey = mappedKey;
    }

    @Basic
    @Column(name = "mapped_value")
    public String getMappedValue() {
        return mappedValue;
    }

    public void setMappedValue(String mappedValue) {
        this.mappedValue = mappedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RdbmsLoggingEventProperty that = (RdbmsLoggingEventProperty) o;

        if (eventId != that.eventId) return false;
        if (mappedKey != null ? !mappedKey.equals(that.mappedKey) : that.mappedKey != null) return false;
        if (mappedValue != null ? !mappedValue.equals(that.mappedValue) : that.mappedValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (mappedKey != null ? mappedKey.hashCode() : 0);
        result = 31 * result + (mappedValue != null ? mappedValue.hashCode() : 0);
        return result;
    }
}
