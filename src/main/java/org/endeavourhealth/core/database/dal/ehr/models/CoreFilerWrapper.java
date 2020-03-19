package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;
import java.util.UUID;

public class CoreFilerWrapper {

    private UUID serviceId;
    private UUID systemId;
    private UUID version;
    private Date createdAt;
    private String dataType;
    private Object data;
    private UUID exchangeBatchId;
    private boolean isDeleted;

    public CoreFilerWrapper() {}

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

    public UUID getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(UUID exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
