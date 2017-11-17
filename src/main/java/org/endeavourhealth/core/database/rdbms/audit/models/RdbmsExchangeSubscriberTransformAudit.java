package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeSubscriberTransformAudit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "exchange_subscriber_transform_audit")
public class RdbmsExchangeSubscriberTransformAudit implements Serializable {

    private String exchangeId = null;
    private String exchangeBatchId = null;
    private String subscriberConfigName = null;
    private Date started = null;
    private Date ended = null;
    private String errorXml = null;
    private Integer numberResourcesTransformed = null;
    private String queuedMessageId = null;

    public RdbmsExchangeSubscriberTransformAudit() {}

    public RdbmsExchangeSubscriberTransformAudit(ExchangeSubscriberTransformAudit proxy) {
        this.exchangeId = proxy.getExchangeId().toString();
        this.exchangeBatchId = proxy.getExchangeBatchId().toString();
        this.subscriberConfigName = proxy.getSubscriberConfigName();
        this.started = proxy.getStarted();
        this.ended = proxy.getEnded();
        this.errorXml = proxy.getErrorXml();
        this.numberResourcesTransformed = proxy.getNumberResourcesTransformed();
        if (proxy.getQueuedMessageId() != null) {
            this.queuedMessageId = proxy.getQueuedMessageId().toString();
        }
    }

    @Id
    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Id
    @Column(name = "exchange_batch_id", nullable = false)
    public String getExchangeBatchId() {
        return exchangeBatchId;
    }

    public void setExchangeBatchId(String exchangeBatchId) {
        this.exchangeBatchId = exchangeBatchId;
    }

    @Id
    @Column(name = "subscriber_config_name", nullable = false)
    public String getSubscriberConfigName() {
        return subscriberConfigName;
    }

    public void setSubscriberConfigName(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Id
    @Column(name = "started", nullable = false)
    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    @Column(name = "ended", nullable = true)
    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }

    @Column(name = "error_xml", nullable = true)
    public String getErrorXml() {
        return errorXml;
    }

    public void setErrorXml(String errorXml) {
        this.errorXml = errorXml;
    }

    @Column(name = "number_resources_transformed", nullable = true)
    public Integer getNumberResourcesTransformed() {
        return numberResourcesTransformed;
    }

    public void setNumberResourcesTransformed(Integer numberResourcesTransformed) {
        this.numberResourcesTransformed = numberResourcesTransformed;
    }

    @Column(name = "queued_message_id", nullable = true)
    public String getQueuedMessageId() {
        return queuedMessageId;
    }

    public void setQueuedMessageId(String queuedMessageId) {
        this.queuedMessageId = queuedMessageId;
    }
}
