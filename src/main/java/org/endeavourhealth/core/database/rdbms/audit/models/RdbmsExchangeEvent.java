package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "exchange_event", schema = "public")
public class RdbmsExchangeEvent implements Serializable {

    private String exchangeId = null;
    private Date timestamp = null;
    private String eventDesc = null;

    public RdbmsExchangeEvent() {}

    public RdbmsExchangeEvent(ExchangeEvent proxy) {
        this.exchangeId = proxy.getExchangeId().toString();
        this.timestamp = proxy.getTimestamp();
        this.eventDesc = proxy.getEventDesc();
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
    @Column(name = "timestamp", nullable = false)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "event_desc", nullable = false)
    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
}
