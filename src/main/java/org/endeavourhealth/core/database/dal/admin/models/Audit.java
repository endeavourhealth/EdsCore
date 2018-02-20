package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsAudit;

import java.util.Date;
import java.util.UUID;

public class Audit {

    private UUID id = null;
    private UUID organisationId = null;
    private Date timestamp = null;
    private UUID endUserId = null;

    public Audit() {}

    /*public Audit(CassandraAudit proxy) {
        this.id = proxy.getId();
        this.organisationId = proxy.getOrganisationId();
        this.timestamp = proxy.getTimeStamp();
        this.endUserId = proxy.getEndUserId();
    }*/

    public Audit(RdbmsAudit proxy) {
        this.id = UUID.fromString(proxy.getId());
        this.organisationId = UUID.fromString(proxy.getOrganisationId());
        this.timestamp = proxy.getTimestamp();
        this.endUserId = UUID.fromString(proxy.getEndUserId());
    }


    public static Audit factoryNow(UUID endUserUuid, UUID organisationUuid) {
        Audit ret = new Audit();
        ret.setId(UUID.randomUUID()); //always explicitly set a new UUID as we'll always want to use it
        ret.setEndUserId(endUserUuid);
        ret.setTimestamp(new Date());
        ret.setOrganisationId(organisationUuid);
        return ret;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(UUID organisationId) {
        this.organisationId = organisationId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getEndUserId() {
        return endUserId;
    }

    public void setEndUserId(UUID endUserId) {
        this.endUserId = endUserId;
    }
}
