package org.endeavourhealth.core.database.dal.publisherTransform.models;

import java.util.Date;
import java.util.UUID;

public class ResourceFieldMapping {
    private UUID resourceId;
    private String resourceType;
    private Date createdAt;
    private UUID version;
    private String resourceField;
    private String sourceFileName;
    private Integer sourceFileRow;
    private Integer sourceFileColumn;
    private String sourceLocation;
    private String value;

    public ResourceFieldMapping() {

    }

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

    public String getSourceFileName() {
        return sourceFileName;
    }

    public ResourceFieldMapping setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
        return this;
    }

    public Integer getSourceFileRow() {
        return sourceFileRow;
    }

    public ResourceFieldMapping setSourceFileRow(Integer sourceFileRow) {
        this.sourceFileRow = sourceFileRow;
        return this;
    }

    public Integer getSourceFileColumn() {
        return sourceFileColumn;
    }

    public ResourceFieldMapping setSourceFileColumn(Integer sourceFileColumn) {
        this.sourceFileColumn = sourceFileColumn;
        return this;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public ResourceFieldMapping setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Field [" + resourceField + "] val [" + value + "] from row " + sourceFileRow + " col " + sourceFileColumn + " of " + sourceFileName;
    }
}
