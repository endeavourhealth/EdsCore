package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;
import java.util.UUID;

public class LastDataToSubscriber {
    private String subscriberConfigName;
    private UUID serviceId;
    private UUID systemId;
    private Date sentDate; //date applied/sent to the subscriber
    private UUID exchangeId;
    private Date extractDate;
    private Date extractCutoff;

    public LastDataToSubscriber() {
    }

    public String getSubscriberConfigName() {
        return subscriberConfigName;
    }

    public void setSubscriberConfigName(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
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

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
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
        return "subscriberConfigName [" + subscriberConfigName + "], "
                + "serviceId [" + serviceId + "], "
                + "systemId [" + systemId + "], "
                + "extractDate [" + extractDate + "], "
                + "extractCutoff [" + extractCutoff + "], "
                + "sentDate [" + sentDate + "], "
                + "exchangeId [" + exchangeId + "]";
    }
}
