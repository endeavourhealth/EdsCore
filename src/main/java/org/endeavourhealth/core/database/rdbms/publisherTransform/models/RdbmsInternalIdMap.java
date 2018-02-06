package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.InternalIdMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "internal_id_map")
public class RdbmsInternalIdMap implements Serializable {

    private String serviceId = null;
    private String idType = null;
    private String sourceId = null;
    private String destinationId = null;
    private Date updatedAt = null;

    public RdbmsInternalIdMap() {}

    public RdbmsInternalIdMap(InternalIdMap r) {
        this.serviceId = r.getServiceId();
        this.idType = r.getIdType();
        this.sourceId = r.getSourceId();
        this.destinationId = r.getDestinationId();
        this.updatedAt = r.getUpdatedAt();
    }

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "id_type", nullable = false)
    public String getIdType() {
        return idType;
    }

    public void setResourceType(String idType) {
        this.idType = idType;
    }

    @Id
    @Column(name = "source_id", nullable = false)
    public String getSourceResourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "destination_id", nullable = false)
    public String getDestinationResourceId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    @Column(name = "updated_at", nullable = false)
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
