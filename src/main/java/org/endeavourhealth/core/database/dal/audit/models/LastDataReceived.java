package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;
import java.util.UUID;

public class LastDataReceived {
    private UUID serviceId;
    private UUID systemId;
    private Date receivedDate;
    private UUID exchangeId;
    private Date extractDate;
    private Date extractCutoff;

    public LastDataReceived() {
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

    public Date getExtractDate() {
        return extractDate;
    }

    public void setExtractDate(Date extractDate) {
        this.extractDate = extractDate;
    }

    public Date getExtractCutoff() {
        return extractCutoff;
    }

    public void setExtractCutoff(Date extractCutoff) {
        this.extractCutoff = extractCutoff;
    }


    @Override
    public String toString() {
        return "serviceId [" + serviceId + "], "
                + "systemId [" + systemId + "], "
                + "extractDate [" + extractDate + "], "
                + "extractCutoff [" + extractCutoff + "], "
                + "receivedDate [" + receivedDate + "], "
                + "exchangeId [" + exchangeId + "]";
    }
}
