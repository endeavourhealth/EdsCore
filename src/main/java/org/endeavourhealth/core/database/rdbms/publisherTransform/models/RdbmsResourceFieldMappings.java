package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource_field_mappings")
public class RdbmsResourceFieldMappings implements Serializable {

    private String resourceId;
    private String resourceType;
    private Date createdAt;
    private String version;
    private String mappingsJson;

    public RdbmsResourceFieldMappings() {

    }

    /*public RdbmsResourceFieldMapping(ResourceFieldMapping proxy) {
        this.resourceId = proxy.getResourceId().toString();
        this.resourceType = proxy.getResourceType();
        this.createdAt = proxy.getCreatedAt();
        this.version = proxy.getVersion().toString();
        this.resourceField = proxy.getResourceField();
        this.sourceFileFieldId = proxy.getSourceFileFieldId();
    }*/


    @Id
    @Column(name = "resource_id", nullable = false)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
    @Column(name = "created_at", nullable = false)
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "version", nullable = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "mappings_json", nullable = false)
    public String getMappingsJson() {
        return mappingsJson;
    }

    public void setMappingsJson(String mappingsJson) {
        this.mappingsJson = mappingsJson;
    }

}
