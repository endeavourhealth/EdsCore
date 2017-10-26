package org.endeavourhealth.core.database.rdbms.logback.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class RdbmsLoggingEventExceptionPK implements Serializable {
    private long eventId;
    private short i;

    @Column(name = "event_id")
    @Id
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Column(name = "i")
    @Id
    public short getI() {
        return i;
    }

    public void setI(short i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RdbmsLoggingEventExceptionPK that = (RdbmsLoggingEventExceptionPK)o;

        if (eventId != that.eventId) return false;
        if (i != that.i) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (int) i;
        return result;
    }
}

