package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeTransformAudit;

import java.util.Date;
import java.util.UUID;

public class ExchangeTransformAudit {

    private UUID id = null;
    private UUID serviceId = null;
    private UUID systemId = null;
    private UUID exchangeId = null;
    private Date started = null;
    private Date ended = null;
    private String errorXml = null;
    private boolean resubmitted = false;
    private Date deleted = null;
    private Integer numberBatchesCreated = null;

    public ExchangeTransformAudit() {}

    /*public ExchangeTransformAudit(CassandraExchangeTransformAudit proxy) {
        this.id = proxy.getVersion();
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.exchangeId = proxy.getExchangeId();
        this.started = proxy.getStarted();
        this.ended = proxy.getEnded();
        this.errorXml = proxy.getErrorXml();
        this.resubmitted = proxy.isResubmitted();
        this.deleted = proxy.getDeleted();
        this.numberBatchesCreated = proxy.getNumberBatchesCreated();

    }*/

    public ExchangeTransformAudit(RdbmsExchangeTransformAudit proxy) {
        this.id = UUID.fromString(proxy.getId());
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.started = proxy.getStarted();
        this.ended = proxy.getEnded();
        this.errorXml = proxy.getErrorXml();
        this.resubmitted = proxy.isResubmitted();
        this.deleted = proxy.getDeleted();
        this.numberBatchesCreated = proxy.getNumberBatchesCreated();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
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

    public boolean isResubmitted() {
        return resubmitted;
    }

    public void setResubmitted(boolean resubmitted) {
        this.resubmitted = resubmitted;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Integer getNumberBatchesCreated() {
        return numberBatchesCreated;
    }

    public void setNumberBatchesCreated(Integer numberBatchesCreated) {
        this.numberBatchesCreated = numberBatchesCreated;
    }

}
