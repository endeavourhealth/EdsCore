package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class RdbmsExchangeProtocolErrorPK implements Serializable {
    private String exchangeId;
    private Timestamp insertedAt;

    @Column(name = "exchange_id")
    @Id
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Column(name = "inserted_at")
    @Id
    public Timestamp getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsExchangeProtocolErrorPK that = (RdbmsExchangeProtocolErrorPK) o;
        return Objects.equals(exchangeId, that.exchangeId) &&
                Objects.equals(insertedAt, that.insertedAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(exchangeId, insertedAt);
    }
}
