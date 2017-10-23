package org.endeavourhealth.core.rdbms.audit.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "exchange_transform_audit", schema = "public", catalog = "audit")
public class ExchangeTransformAudit {

    private String serviceId = null;
    private String systemId = null;
    private String exchangeId = null;
    private DateTime timestamp = null;
    private DateTime started = null;
    private DateTime ended = null;
    private String errorXml = null;
    private boolean resubmitted = false;
    private DateTime deleted = null;
    private int numberBatchesCreated = -1;

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "system_id", nullable = false)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
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
    @Column(name = "timestamp", nullable = false)
    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "started", nullable = true)
    public DateTime getStarted() {
        return started;
    }

    public void setStarted(DateTime started) {
        this.started = started;
    }

    @Column(name = "ended", nullable = true)
    public DateTime getEnded() {
        return ended;
    }

    public void setEnded(DateTime ended) {
        this.ended = ended;
    }

    @Column(name = "error_xml", nullable = true)
    public String getErrorXml() {
        return errorXml;
    }

    public void setErrorXml(String errorXml) {
        this.errorXml = errorXml;
    }

    @Column(name = "resubmitted", nullable = true)
    public boolean isResubmitted() {
        return resubmitted;
    }

    public void setResubmitted(boolean resubmitted) {
        this.resubmitted = resubmitted;
    }

    @Column(name = "deleted", nullable = true)
    public DateTime getDeleted() {
        return deleted;
    }

    public void setDeleted(DateTime deleted) {
        this.deleted = deleted;
    }

    @Column(name = "number_batches_created", nullable = true)
    public int getNumberBatchesCreated() {
        return numberBatchesCreated;
    }

    public void setNumberBatchesCreated(int numberBatchesCreated) {
        this.numberBatchesCreated = numberBatchesCreated;
    }

}
