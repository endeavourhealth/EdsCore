package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsInternalIdMap;

import java.util.Date;

public class InternalIdMap {

    private String serviceId = null;
    private String idType = null;
    private String sourceId = null;
    private String destinationId = null;
    private Date updatedAt = null;

    public InternalIdMap(RdbmsInternalIdMap r) {
        this.serviceId = r.getServiceId();
        this.idType = r.getIdType();
        this.sourceId = r.getSourceResourceId();
        this.destinationId = r.getDestinationResourceId();
        this.updatedAt = r.getUpdatedAt();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}