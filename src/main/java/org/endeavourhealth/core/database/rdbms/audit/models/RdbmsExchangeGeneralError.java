package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeGeneralError;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "exchange_general_error")
@IdClass(RdbmsExchangeGeneralErrorPK.class)
public class RdbmsExchangeGeneralError {
    private String exchangeId;
    private Date insertedAt;
    private String errorMessage;

    public RdbmsExchangeGeneralError() {}

    public RdbmsExchangeGeneralError(ExchangeGeneralError proxy) {
        this.exchangeId = proxy.getExchangeId().toString();
        this.insertedAt = proxy.getInsertedAt();
        this.errorMessage = proxy.getErrorMessage();
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

    @Basic
    @Column(name = "error_message")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsExchangeGeneralError that = (RdbmsExchangeGeneralError) o;
        return Objects.equals(exchangeId, that.exchangeId) &&
                Objects.equals(insertedAt, that.insertedAt) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(exchangeId, insertedAt, errorMessage);
    }
}
