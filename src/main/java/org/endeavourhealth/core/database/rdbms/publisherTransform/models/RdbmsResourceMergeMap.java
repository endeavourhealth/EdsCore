package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource_merge_map")
public class RdbmsResourceMergeMap implements Serializable {

    private String serviceId = null;
    private String sourceResourceId = null;
    private String destinationResourceId = null;
    private Date updatedAt = null;

    public RdbmsResourceMergeMap() {}

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "source_resource_id", nullable = false)
    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(String sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    @Column(name = "destination_resource_id", nullable = false)
    public String getDestinationResourceId() {
        return destinationResourceId;
    }

    public void setDestinationResourceId(String destinationResourceId) {
        this.destinationResourceId = destinationResourceId;
    }

    @Column(name = "updated_at", nullable = false)
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
