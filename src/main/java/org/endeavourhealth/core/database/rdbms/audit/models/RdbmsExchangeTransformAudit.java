package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformAudit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "exchange_transform_audit", schema = "public")
public class RdbmsExchangeTransformAudit implements Serializable {

    private String id = null;
    private String serviceId = null;
    private String systemId = null;
    private String exchangeId = null;
    private Date started = null;
    private Date ended = null;
    private String errorXml = null;
    private boolean resubmitted = false;
    private Date deleted = null;
    private Integer numberBatchesCreated = -1;

    public RdbmsExchangeTransformAudit() {}

    public RdbmsExchangeTransformAudit(ExchangeTransformAudit proxy) {
        this.id = proxy.getId().toString();
        this.serviceId = proxy.getServiceId().toString();
        this.systemId = proxy.getSystemId().toString();
        this.exchangeId = proxy.getExchangeId().toString();
        this.started = proxy.getStarted();
        this.ended = proxy.getEnded();
        this.errorXml = proxy.getErrorXml();
        this.resubmitted = proxy.isResubmitted();
        this.deleted = proxy.getDeleted();
        this.numberBatchesCreated = proxy.getNumberBatchesCreated();
    }

    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
    @Column(name = "started", nullable = true)
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

    @Column(name = "resubmitted", nullable = true)
    public boolean isResubmitted() {
        return resubmitted;
    }

    public void setResubmitted(boolean resubmitted) {
        this.resubmitted = resubmitted;
    }

    @Column(name = "deleted", nullable = true)
    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Column(name = "number_batches_created", nullable = true)
    public Integer getNumberBatchesCreated() {
        return numberBatchesCreated;
    }

    public void setNumberBatchesCreated(Integer numberBatchesCreated) {
        this.numberBatchesCreated = numberBatchesCreated;
    }

}
