package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeGeneralError;

import java.util.Date;
import java.util.UUID;

public class ExchangeGeneralError {

    private UUID exchangeId = null;
    private Date insertedAt = null;
    private String errorMessage = null;

    public ExchangeGeneralError() {}

    public ExchangeGeneralError(RdbmsExchangeGeneralError proxy) {
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.insertedAt = proxy.getInsertedAt();
        this.errorMessage = proxy.getErrorMessage();
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
