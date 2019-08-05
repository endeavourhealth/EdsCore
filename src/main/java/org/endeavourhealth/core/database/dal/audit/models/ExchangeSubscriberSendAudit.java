package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.xml.transformError.TransformError;

import java.util.Date;
import java.util.UUID;

public class ExchangeSubscriberSendAudit {

    private UUID exchangeId;
    private UUID exchangeBatchId;
    private String subscriberConfigName;
    private Date insertedAt;
    private TransformError error;
    private UUID queuedMessageId;

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public UUID getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(UUID exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }

    public String getSubscriberConfigName() {
        return subscriberConfigName;
    }

    public void setSubscriberConfigName(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public TransformError getError() {
        return error;
    }

    public void setError(TransformError error) {
        this.error = error;
    }

    public UUID getQueuedMessageId() {
        return queuedMessageId;
    }

    public void setQueuedMessageId(UUID queuedMessageId) {
        this.queuedMessageId = queuedMessageId;
    }
}
