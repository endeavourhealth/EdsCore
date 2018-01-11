package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "resource_id_map")
public class RdbmsResourceIdMap implements Serializable {

    private String serviceId = null;
    private String resourceType = null;
    private String sourceId = null;
    private String edsId = null;

    public RdbmsResourceIdMap() {}

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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
    @Column(name = "source_id", nullable = false)
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "eds_id", nullable = false)
    public String getEdsId() {
        return edsId;
    }

    public void setEdsId(String edsId) {
        this.edsId = edsId;
    }

}
