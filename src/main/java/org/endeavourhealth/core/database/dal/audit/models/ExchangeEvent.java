package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeEvent;

import java.util.Date;
import java.util.UUID;

public class ExchangeEvent {

    private UUID exchangeId = null;
    private Date timestamp = null;
    private String eventDesc = null;

    public ExchangeEvent() {}

    /*public ExchangeEvent(CassandraExchangeEvent proxy) {
        this.exchangeId = proxy.getExchangeId();
        this.timestamp = proxy.getTimestamp();
        this.eventDesc = proxy.getEventDesc();
    }*/

    public ExchangeEvent(RdbmsExchangeEvent proxy) {
        this.exchangeId = UUID.fromString(proxy.getExchangeId());
        this.timestamp = proxy.getTimestamp();
        this.eventDesc = proxy.getEventDesc();
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
}
