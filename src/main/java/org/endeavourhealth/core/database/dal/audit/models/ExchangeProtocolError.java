package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeProtocolError;

import java.util.Date;
import java.util.UUID;

public class ExchangeProtocolError {

    private UUID exchangeId = null;
    private Date insertedAt = null;

    public ExchangeProtocolError() {}

    public ExchangeProtocolError(RdbmsExchangeProtocolError proxy) {
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.insertedAt = proxy.getInsertedAt();
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
}
