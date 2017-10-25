package org.endeavourhealth.core.database.rdbms.hl7receiver.models;

import org.endeavourhealth.core.database.dal.hl7receiver.models.ResourceId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "resource_uuid", schema = "mapping")
public class RdbmsResourceId implements Serializable {

    private String scopeId = null;
    private String resourceType = null;
    private String uniqueId = null;
    private UUID resourceId = null;

    public RdbmsResourceId() {}

    public RdbmsResourceId(ResourceId proxy) {
        this.scopeId = proxy.getScopeId();
        this.resourceType = proxy.getResourceType();
        this.uniqueId = proxy.getUniqueId();
        this.resourceId = proxy.getResourceId();
    }

    @Id
    @Column(name = "scope_id", nullable = false)
    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }


    @Id
    @Column(name = "resource_type", nullable = false)
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


    @Id
    @Column(name = "unique_identifier", nullable = false)
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


    @Column(name = "resource_uuid", nullable = false)
    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }
}
