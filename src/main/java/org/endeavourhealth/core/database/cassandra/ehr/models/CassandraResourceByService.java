package org.endeavourhealth.core.database.cassandra.ehr.models;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;

import java.util.Date;
import java.util.UUID;

@Table(keyspace = "ehr", name = "resource_by_service")
public class CassandraResourceByService {

    @PartitionKey(0)
    @Column(name = "service_id")
    private UUID serviceId;
    @PartitionKey(1)
    @Column(name = "system_id")
    private UUID systemId;
    @PartitionKey(2)
    @Column(name = "resource_type")
    private String resourceType;
    @ClusteringColumn
    @Column(name = "resource_id")
    private UUID resourceId;
    @Column(name = "current_version")
    private UUID currentVersion;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "patient_id")
    private UUID patientId;
    @Column(name = "schema_version")
    private String schemaVersion;
    @Column(name = "resource_metadata")
    private String resourceMetadata;
    @Column(name = "resource_data")
    private String resourceData;
    
    public CassandraResourceByService() {}

    public CassandraResourceByService(ResourceWrapper proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceType = proxy.getResourceType();
        this.resourceId = proxy.getResourceId();
        this.currentVersion = proxy.getVersion();
        this.updatedAt = proxy.getCreatedAt();
        this.patientId = proxy.getPatientId();
        //this.schemaVersion = proxy. //not present in proxy
        this.resourceMetadata = proxy.getResourceMetadata();
        this.resourceData = proxy.getResourceData();
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public UUID getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(UUID currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getResourceMetadata() {
        return resourceMetadata;
    }

    public void setResourceMetadata(String resourceMetadata) {
        this.resourceMetadata = resourceMetadata;
    }

    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }
}
