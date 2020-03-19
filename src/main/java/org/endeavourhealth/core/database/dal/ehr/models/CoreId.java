package org.endeavourhealth.core.database.dal.ehr.models;

import java.io.Serializable;
import java.util.UUID;

public class CoreId implements Serializable {

    private UUID serviceId;
    private byte coreTable;
    private int coreId;
    private String sourceId;

    public CoreId(UUID serviceId, byte coreTable, int coreId, String sourceId) {
        this.serviceId = serviceId;
        this.coreTable = coreTable;
        this.coreId = coreId;
        this.sourceId = sourceId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public byte getCoreTable() {
        return coreTable;
    }

    public void setCoreTable(byte coreTable) {
        this.coreTable = coreTable;
    }

    public int getCoreId() {
        return coreId;
    }

    public void setCoreId(int coreId) {
        this.coreId = coreId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "ServiceId " + serviceId + " CoreId " + coreId + ", CoreTable " + coreTable + ", SourceId " + sourceId;
    }
}
