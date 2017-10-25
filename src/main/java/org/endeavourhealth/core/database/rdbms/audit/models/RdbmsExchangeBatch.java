package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource_current", schema = "public")
public class RdbmsExchangeBatch implements Serializable {

    private String exchangeId = null;
    private String batchId = null;
    private Date insertedAt = null;
    private String edsPatientId = null;

    public RdbmsExchangeBatch() {}

    public RdbmsExchangeBatch(ExchangeBatch proxy) {
        this.exchangeId = proxy.getExchangeId().toString();
        this.batchId = proxy.getBatchId().toString();
        this.insertedAt = proxy.getInsertedAt();
        this.edsPatientId = proxy.getEdsPatientId().toString();
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
    @Column(name = "batch_id", nullable = false)
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Column(name = "inserted_at", nullable = false)
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Column(name = "eds_patient_id", nullable = true)
    public String getEdsPatientId() {
        return edsPatientId;
    }

    public void setEdsPatientId(String edsPatientId) {
        this.edsPatientId = edsPatientId;
    }
}
