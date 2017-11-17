package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "enterprise_instance_map")
public class RdbmsEnterpriseInstanceMap implements Serializable {

    private String resourceType = null;
    private String resourceIdFrom = null;
    private String resourceIdTo = null;
    private String mappingValue = null;

    @Id
    @Column(name = "resource_type", nullable = false)
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Id
    @Column(name = "resource_id_from", nullable = false)
    public String getResourceIdFrom() {
        return resourceIdFrom;
    }

    public void setResourceIdFrom(String resourceIdFrom) {
        this.resourceIdFrom = resourceIdFrom;
    }

    @Column(name = "resource_id_to", nullable = true)
    public String getResourceIdTo() {
        return resourceIdTo;
    }

    public void setResourceIdTo(String resourceIdTo) {
        this.resourceIdTo = resourceIdTo;
    }

    @Column(name = "mapping_value", nullable = true)
    public String getMappingValue() {
        return mappingValue;
    }

    public void setMappingValue(String mappingValue) {
        this.mappingValue = mappingValue;
    }
}
