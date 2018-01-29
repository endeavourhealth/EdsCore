package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource_field_mapping")
public class RdbmsResourceFieldMapping implements Serializable {

    private String resourceId;
    private String resourceType;
    private Date createdAt;
    private String version;
    private String resourceField;
    private long sourceFileFieldId;

    public RdbmsResourceFieldMapping() {

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

    @Id
    @Column(name = "resource_field", nullable = false)
    public String getResourceField() {
        return resourceField;
    }

    public void setResourceField(String resourceField) {
        this.resourceField = resourceField;
    }

    @Column(name = "source_file_field_id", nullable = false)
    public long getSourceFileFieldId() {
        return sourceFileFieldId;
    }

    public void setSourceFileFieldId(long sourceFileFieldId) {
        this.sourceFileFieldId = sourceFileFieldId;
    }
}
