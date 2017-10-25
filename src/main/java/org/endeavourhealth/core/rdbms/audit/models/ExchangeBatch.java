package org.endeavourhealth.core.rdbms.audit.models;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "resource_current", schema = "public", catalog = "audit")
public class ExchangeBatch  implements Serializable {

    private String exchangeId = null;
    private String batchId = null;
    private DateTime insertedAt = null;
    private String edsPatientId = null;

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
    public DateTime getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(DateTime insertedAt) {
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
