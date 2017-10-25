package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.core.database.dal.admin.models.Audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "audit", schema = "public")
public class RdbmsAudit implements Serializable {

    private String id = null;
    private String organisationId = null;
    private Date timestamp = null;
    private String endUserId = null;

    public RdbmsAudit() {}

    public RdbmsAudit(Audit proxy) {
        this.id = proxy.getId().toString();
        this.organisationId = proxy.getOrganisationId().toString();
        this.timestamp = proxy.getTimestamp();
        this.endUserId = proxy.getEndUserId().toString();
    }

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "organisation_id", nullable = false)
    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    @Id
    @Column(name = "timestamp", nullable = false)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "end_user_id", nullable = false)
    public String getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(String endUserId) {
        this.endUserId = endUserId;
    }


}
