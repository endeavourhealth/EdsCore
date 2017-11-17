package org.endeavourhealth.core.database.dal.audit.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeSubscriberTransformAudit;

import java.util.Date;
import java.util.UUID;

public class ExchangeSubscriberTransformAudit {

    private UUID exchangeId = null;
    private UUID exchangeBatchId = null;
    private String subscriberConfigName = null;
    private Date started = null;
    private Date ended = null;
    private String errorXml = null;
    private Integer numberResourcesTransformed = null;
    private UUID queuedMessageId = null;

    public ExchangeSubscriberTransformAudit() {}

    public ExchangeSubscriberTransformAudit(RdbmsExchangeSubscriberTransformAudit proxy) {
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.exchangeBatchId = UUID.fromString(proxy.getExchangeBatchId());
        this.subscriberConfigName = proxy.getSubscriberConfigName();
        this.started = proxy.getStarted();
        this.ended = proxy.getEnded();
        this.errorXml = proxy.getErrorXml();
        this.numberResourcesTransformed = proxy.getNumberResourcesTransformed();
        if (!Strings.isNullOrEmpty(proxy.getQueuedMessageId())) {
            this.queuedMessageId = UUID.fromString(proxy.getQueuedMessageId());
        }
    }

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

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    public String getErrorXml() {
        return errorXml;
    }

    public void setErrorXml(String errorXml) {
        this.errorXml = errorXml;
    }

    public Integer getNumberResourcesTransformed() {
        return numberResourcesTransformed;
    }

    public void setNumberResourcesTransformed(Integer numberResourcesTransformed) {
        this.numberResourcesTransformed = numberResourcesTransformed;
    }

    public UUID getQueuedMessageId() {
        return queuedMessageId;
    }

    public void setQueuedMessageId(UUID queuedMessageId) {
        this.queuedMessageId = queuedMessageId;
    }
}
