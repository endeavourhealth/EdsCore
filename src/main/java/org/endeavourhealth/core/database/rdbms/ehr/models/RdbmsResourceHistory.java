package org.endeavourhealth.core.database.rdbms.ehr.models;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource_history")
public class RdbmsResourceHistory implements Serializable {

    private String serviceId = null;
    private String systemId = null;
    private String resourceType = null;
    private String resourceId = null;
    private Date createdAt = null;
    private String patientId = null;
    private String resourceData = null;
    private Long resourceChecksum = null;
    private boolean isDeleted = false;
    private String exchangeBatchId = null;
    private String version = null;
    
    public RdbmsResourceHistory() {}
    
    public RdbmsResourceHistory(ResourceWrapper proxy) {
        this.serviceId = proxy.getServiceId().toString();
        this.systemId = proxy.getSystemId().toString();
        this.resourceType = proxy.getResourceType();
        this.resourceId = proxy.getResourceId().toString();
        this.createdAt = proxy.getCreatedAt();
        if (proxy.getPatientId() != null) {
            this.setPatientId(proxy.getPatientId().toString());
        } else {
            //the patient ID is part of the primary key on one of the tables so can't be null
            this.setPatientId("");
        }
        this.resourceData = proxy.getResourceData();
        this.resourceChecksum = proxy.getResourceChecksum();
        this.isDeleted = proxy.isDeleted();
        this.exchangeBatchId = proxy.getExchangeBatchId().toString();
        this.version = proxy.getVersion().toString();
    }

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
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    @Id
    @Column(name = "version", nullable = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
