package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsLastDataProcessed;

import java.util.Date;
import java.util.UUID;

public class LastDataProcessed {
    private UUID serviceId;
    private UUID systemId;
    private Date dataDate;
    private Date processedDate;
    private UUID exchangeId;

    public LastDataProcessed() {
    }

    public LastDataProcessed(RdbmsLastDataProcessed proxy) {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.dataDate = proxy.getDataDate();
        this.processedDate = proxy.getProcessedDate();
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

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }
}

