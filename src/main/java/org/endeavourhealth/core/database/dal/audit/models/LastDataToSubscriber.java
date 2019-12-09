package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;
import java.util.UUID;

public class LastDataToSubscriber {
    private String subscriberConfigName;
    private UUID serviceId;
    private UUID systemId;
    private Date dataDate; //date of the source data (not the date received into DDS)
    private Date sentDate; //date applied/sent to the subscriber
    private UUID exchangeId;

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

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
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

    @Override
    public String toString() {
        return "subscriberConfigName [" + subscriberConfigName + "], "
                + "serviceId [" + serviceId + "], "
                + "systemId [" + systemId + "], "
                + "dataDate [" + dataDate + "], "
                + "sentDate [" + sentDate + "], "
                + "exchangeId [" + exchangeId + "]";
    }
}
