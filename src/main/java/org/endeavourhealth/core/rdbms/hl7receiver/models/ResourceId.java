package org.endeavourhealth.core.rdbms.hl7receiver.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "resource_uuid", schema = "mapping", catalog = "hl7receiver")
public class ResourceId implements Serializable {

    private String scopeId = null;
    private String resourceType = null;
    private String uniqueId = null;
    private UUID resourceId = null;

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
