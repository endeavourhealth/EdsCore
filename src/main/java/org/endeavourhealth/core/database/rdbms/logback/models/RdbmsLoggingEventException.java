package org.endeavourhealth.core.database.rdbms.logback.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "logging_event_exception")
@IdClass(RdbmsLoggingEventExceptionPK.class)
public class RdbmsLoggingEventException implements Serializable {

    private long eventId;
    private short i;
    private String traceLine;

    @Id
    @Column(name = "event_id")
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Id
    @Column(name = "i")
    public short getI() {
        return i;
    }

    public void setI(short i) {
        this.i = i;
    }

    @Basic
    @Column(name = "trace_line")
    public String getTraceLine() {
        return traceLine;
    }

    public void setTraceLine(String traceLine) {
        this.traceLine = traceLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RdbmsLoggingEventException that = (RdbmsLoggingEventException)o;

        if (eventId != that.eventId) return false;
        if (i != that.i) return false;
        if (traceLine != null ? !traceLine.equals(that.traceLine) : that.traceLine != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (int) i;
        result = 31 * result + (traceLine != null ? traceLine.hashCode() : 0);
        return result;
    }
}
