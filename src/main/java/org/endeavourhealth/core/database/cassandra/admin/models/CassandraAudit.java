package org.endeavourhealth.core.database.cassandra.admin.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.admin.models.Audit;

import java.util.Date;
import java.util.UUID;

@Table(keyspace = "admin", name = "audit")
public class CassandraAudit {
    @PartitionKey
    @Column(name = "id")
    private UUID id;
    @Column(name = "end_user_id")
    private UUID endUserId;
    @Column(name = "time_stamp")
    private Date timeStamp;
    @Column(name = "audit_version")
    private Integer auditVersion;
    @Column(name = "organisation_id")
    private UUID organisationId;

    public CassandraAudit() {}

    public CassandraAudit(Audit proxy) {
        this.id = proxy.getId();
        this.organisationId = proxy.getOrganisationId();
        this.timeStamp = proxy.getTimestamp();
        this.endUserId = proxy.getEndUserId();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(UUID endUserId) {
        this.endUserId = endUserId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getAuditVersion() {
        return auditVersion;
    }

    public void setAuditVersion(Integer auditVersion) {
        this.auditVersion = auditVersion;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(UUID organisationId) {
        this.organisationId = organisationId;
    }

}