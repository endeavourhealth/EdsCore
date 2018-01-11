package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "sus_resource_map")
public class RdbmsBartsSusResourceMap implements Serializable {

    private String serviceId = null;
    private String sourceRowId = null;
    private String destinationResourceType = null;
    private String destinationResourceId = null;

    public RdbmsBartsSusResourceMap() {}

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "source_row_id", nullable = false)
    public String getSourceRowId() {
        return sourceRowId;
    }

    public void setSourceRowId(String sourceRowId) {
        this.sourceRowId = sourceRowId;
    }

    @Id
    @Column(name = "destination_resource_type", nullable = false)
    public String getDestinationResourceType() {
        return destinationResourceType;
    }

    public void setDestinationResourceType(String destinationResourceType) {
        this.destinationResourceType = destinationResourceType;
    }

    @Id
    @Column(name = "destination_resource_id", nullable = false)
    public String getDestinationResourceId() {
        return destinationResourceId;
    }

    public void setDestinationResourceId(String destinationResourceId) {
        this.destinationResourceId = destinationResourceId;
    }

}
