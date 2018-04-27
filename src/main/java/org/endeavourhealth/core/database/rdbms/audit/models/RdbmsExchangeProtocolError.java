package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeProtocolError;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "exchange_protocol_error")
@IdClass(RdbmsExchangeProtocolErrorPK.class)
public class RdbmsExchangeProtocolError {
    private String exchangeId;
    private Date insertedAt;

    public RdbmsExchangeProtocolError() {}

    public RdbmsExchangeProtocolError(ExchangeProtocolError proxy) {
        this.exchangeId = proxy.getExchangeId().toString();
        this.insertedAt = proxy.getInsertedAt();
    }

    @Id
    @Column(name = "exchange_id")
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Id
    @Column(name = "inserted_at")
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsExchangeProtocolError that = (RdbmsExchangeProtocolError) o;
        return Objects.equals(exchangeId, that.exchangeId) &&
                Objects.equals(insertedAt, that.insertedAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(exchangeId, insertedAt);
    }
}
