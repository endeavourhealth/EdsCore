package org.endeavourhealth.core.rdbms.ehr.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resource_history", schema = "public", catalog = "ehr")
public class ResourceHistory {

    private String serviceId = null;
    private String systemId = null;
    private String resourceType = null;
    private String resourceId = null;
    private DateTime createdAt = null;
    private String patientId = null;
    private String resourceData = null;
    private Long resourceChecksum = null;
    private boolean isDeleted = false;
    private String exchangeBatchId = null;

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "system_id", nullable = false)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
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
    @Column(name = "resource_id", nullable = false)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Id
    @Column(name = "created_at", nullable = false)
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "patient_id", nullable = true)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Column(name = "resource_data", nullable = true)
    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }

    @Column(name = "resource_checksum", nullable = true)
    public Long getResourceChecksum() {
        return resourceChecksum;
    }

    public void setResourceChecksum(Long resourceChecksum) {
        this.resourceChecksum = resourceChecksum;
    }

    @Column(name = "is_deleted", nullable = true)
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Column(name = "exchange_batch_id", nullable = false)
    public String getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(String exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }
}
