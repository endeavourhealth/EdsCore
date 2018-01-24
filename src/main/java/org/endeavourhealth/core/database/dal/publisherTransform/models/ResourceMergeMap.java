package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceMergeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

public class ResourceMergeMap {

    private String serviceId = null;
    private String resourceType = null;
    private String sourceResourceId = null;
    private String destinationResourceId = null;
    private Date updatedAt = null;

    public ResourceMergeMap(RdbmsResourceMergeMap r) {
        this.serviceId = r.getServiceId();
        this.resourceType = r.getResourceType();
        this.sourceResourceId = r.getSourceResourceId();
        this.destinationResourceId = r.getDestinationResourceId();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(String sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    public String getDestinationResourceId() {
        return destinationResourceId;
    }

    public void setDestinationResourceId(String destinationResourceId) {
        this.destinationResourceId = destinationResourceId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
