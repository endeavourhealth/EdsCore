package org.endeavourhealth.core.database.dal.publisherTransform.models;

import java.util.Date;
import java.util.UUID;

public class ResourceFieldMapping {
    private UUID resourceId;
    private String resourceType;
    private Date createdAt;
    private UUID version;
    private String resourceField;
    private long sourceFileFieldId;
    private String value;

    public ResourceFieldMapping() {

    }

    /*public ResourceFieldMapping(RdbmsResourceFieldMapping proxy) {
        this.resourceId = UUID.fromString(proxy.getResourceId());
        this.resourceType = proxy.getResourceType();
        this.createdAt = proxy.getCreatedAt();
        this.version = UUID.fromString(proxy.getVersion());
        this.resourceField = proxy.getResourceField();
        this.sourceFileFieldId = proxy.getSourceFileFieldId();
    }*/

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getVersion() {
        return version;
    }

    public void setVersion(UUID version) {
        this.version = version;
    }

    public String getResourceField() {
        return resourceField;
    }

    public void setResourceField(String resourceField) {
        this.resourceField = resourceField;
    }

    public long getSourceFileFieldId() {
        return sourceFileFieldId;
    }

    public void setSourceFileFieldId(long sourceFileFieldId) {
        this.sourceFileFieldId = sourceFileFieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
