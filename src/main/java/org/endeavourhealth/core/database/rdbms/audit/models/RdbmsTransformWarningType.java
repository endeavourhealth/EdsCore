package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transform_warning_type")
public class RdbmsTransformWarningType {

    private int id;
    private String warning;
    private Date lastUsedAt;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "warning", nullable =  false)
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    @Column(name = "last_used_at", nullable =  false)
    public Date getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Date lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
}
