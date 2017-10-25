package org.endeavourhealth.core.database.dal.hl7receiver.models;

import org.endeavourhealth.core.database.rdbms.hl7receiver.models.RdbmsResourceId;

import java.util.UUID;

public class ResourceId {

    private String scopeId = null;
    private String resourceType = null;
    private String uniqueId = null;
    private UUID resourceId = null;

    public ResourceId() {}

    public ResourceId(RdbmsResourceId proxy) {
        this.scopeId = proxy.getScopeId();
        this.resourceType = proxy.getResourceType();
        this.uniqueId = proxy.getUniqueId();
        this.resourceId = proxy.getResourceId();
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }
}
