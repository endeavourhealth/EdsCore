package org.endeavourhealth.core.database.dal.audit.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeBatch;

import java.util.Date;
import java.util.UUID;

public class ExchangeBatch {

    private UUID exchangeId = null;
    private UUID batchId = null;
    private Date insertedAt = null;
    private UUID edsPatientId = null;
    private boolean needsSaving = true;

    public ExchangeBatch() {}

    /*public ExchangeBatch(CassandraExchangeBatch proxy) {
        this.exchangeId = proxy.getExchangeId();
        this.batchId = proxy.getBatchId();
        this.insertedAt = proxy.getInsertedAt();
        this.edsPatientId = proxy.getEdsPatientId();
    }*/

    public ExchangeBatch(RdbmsExchangeBatch proxy) {
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.batchId = UUID.fromString(proxy.getBatchId());
        this.insertedAt = proxy.getInsertedAt();
        if (!Strings.isNullOrEmpty(proxy.getEdsPatientId())) {
            this.edsPatientId = UUID.fromString(proxy.getEdsPatientId());
        }
        this.needsSaving = false; //if it's come from the DB, it doesn't need saving
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public UUID getBatchId() {
        return batchId;
    }

    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public UUID getEdsPatientId() {
        return edsPatientId;
    }

    public void setEdsPatientId(UUID edsPatientId) {
        this.edsPatientId = edsPatientId;
    }

    public boolean isNeedsSaving() {
        return needsSaving;
    }

    public void setNeedsSaving(boolean needsSaving) {
        this.needsSaving = needsSaving;
    }
}
