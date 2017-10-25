package org.endeavourhealth.core.database.dal.ehr.models;

import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByExchangeBatch;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByPatient;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceByService;
import org.endeavourhealth.core.database.cassandra.ehr.models.CassandraResourceHistory;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceCurrent;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceHistory;

import java.util.Date;
import java.util.UUID;

public class ResourceWrapper {

    private UUID serviceId;
    private UUID systemId;
    private UUID resourceId;
    private String resourceType;
    private UUID version;
    private Date createdAt;
    private UUID patientId;
    //private String schemaVersion;
    private String resourceMetadata;
    private String resourceData;
    private long resourceChecksum;
    private UUID exchangeBatchId;
    private UUID exchangeId;
    private boolean isDeleted;

    public ResourceWrapper() {}

    public ResourceWrapper(CassandraResourceByExchangeBatch proxy) {
        //this.serviceId = proxy.getServiceId(); //not present in the proxy
        //this.systemId = proxy.getSystemId(); //not present in the proxy
        this.resourceId = proxy.getResourceId();
        this.resourceType = proxy.getResourceType();
        this.version = proxy.getVersion();
        //this.createdAt = proxy.getUpdatedAt(); //not present in the proxy
        //this.patientId = proxy.getPatientId(); //not present in the proxy
        //this.resourceMetadata = proxy.getResourceMetadata(); //not present in the proxy
        this.resourceData = proxy.getResourceData();
        //this.resourceChecksum = proxy.getResourceChecksum(); //not present in the proxy
        this.exchangeBatchId = proxy.getBatchId();
        this.exchangeId = proxy.getExchangeId();
        this.isDeleted = proxy.getIsDeleted();
    }

    public ResourceWrapper(CassandraResourceByPatient proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceId = proxy.getResourceId();
        this.resourceType = proxy.getResourceType();
        //this.version = proxy.getVersion(); //not present in proxy
        //this.createdAt = proxy.getUpdatedAt(); //not present in the proxy
        this.patientId = proxy.getPatientId();
        this.resourceMetadata = proxy.getResourceMetadata();
        this.resourceData = proxy.getResourceData();
        // //not present in the proxythis.resourceChecksum = proxy.getResourceChecksum();
        //this.exchangeBatchId = proxy  //not present in the proxy
        //this.exchangeId = proxy.getExchangeId(); //this proxy doesn't have this field
        //this.isDeleted = proxy  //not present in the proxy
    }

    public ResourceWrapper(CassandraResourceByService proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceId = proxy.getResourceId();
        this.resourceType = proxy.getResourceType();
        //this.version = proxy.getVersion(); //not present in proxy
        this.createdAt = proxy.getUpdatedAt();
        this.patientId = proxy.getPatientId();
        this.resourceMetadata = proxy.getResourceMetadata();
        this.resourceData = proxy.getResourceData();
        //this.resourceChecksum = proxy.getResourceChecksum(); //not present in the proxy
        //this.exchangeBatchId = proxy.  //not present in the proxy
        //this.exchangeId = proxy.getExchangeId(); //this proxy doesn't have this field
        //this.isDeleted = proxy  //not present in the proxy
    }

    public ResourceWrapper(CassandraResourceHistory proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.resourceId = proxy.getResourceId();
        this.resourceType = proxy.getResourceType();
        this.version = proxy.getVersion();
        this.createdAt = proxy.getCreatedAt();
        //this.patientId = proxy.getPatientId(); //not present in the proxy
        //this.resourceMetadata = proxy.getResourceMetadata(); //not present in the proxy
        this.resourceData = proxy.getResourceData();
        this.resourceChecksum = proxy.getResourceChecksum();
        //this.exchangeBatchId = proxy.  //not present in the proxy
        //this.exchangeId = proxy.getExchangeId(); //this proxy doesn't have this field
        this.isDeleted = proxy.getIsDeleted();
    }

    public ResourceWrapper(RdbmsResourceCurrent proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.resourceId = UUID.fromString(proxy.getResourceId());
        this.resourceType = proxy.getResourceType();
        //this.version = proxy.getVersion(); //not present in proxy
        this.createdAt = proxy.getUpdatedAt();
        this.patientId = UUID.fromString(proxy.getPatientId());
        this.resourceMetadata = proxy.getResourceMetadata();
        this.resourceData = proxy.getResourceData();
        this.resourceChecksum = proxy.getResourceChecksum();
        //this.exchangeBatchId = proxy. //this proxy object doesn't have this field
        //this.exchangeId = proxy.getExchangeId(); //this proxy doesn't have this field
        //this.isDeleted = proxy //this proxy object doesn't have this field
    }

    public ResourceWrapper(RdbmsResourceHistory proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.resourceId = UUID.fromString(proxy.getResourceId());
        this.resourceType = proxy.getResourceType();
        //this.version = proxy.getVersion(); //not present in proxy
        this.createdAt = proxy.getCreatedAt();
        this.patientId = UUID.fromString(proxy.getPatientId());
        //this.resourceMetadata = proxy.getResourceMetadata(); //this proxy doesn't have this field
        this.resourceData = proxy.getResourceData();
        this.resourceChecksum = proxy.getResourceChecksum();
        this.exchangeBatchId = UUID.fromString(proxy.getExchangeBatchId());
        //this.exchangeId = proxy.getExchangeId(); //this proxy doesn't have this field
        this.isDeleted = proxy.isDeleted();
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

    public UUID getVersion() {
        return version;
    }

    public void setVersion(UUID version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
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

    public long getResourceChecksum() {
        return resourceChecksum;
    }

    public void setResourceChecksum(long resourceChecksum) {
        this.resourceChecksum = resourceChecksum;
    }

    public UUID getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(UUID exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
