package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsLastDataReceived;

import java.util.Date;
import java.util.UUID;

public class LastDataReceived {
    private UUID serviceId;
    private UUID systemId;
    private Date dataDate;
    private Date receivedDate;
    private UUID exchangeId;

    public LastDataReceived() {
    }

    public LastDataReceived(RdbmsLastDataReceived proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.dataDate = proxy.getDataDate();
        this.receivedDate = proxy.getReceivedDate();
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
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

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }
}
