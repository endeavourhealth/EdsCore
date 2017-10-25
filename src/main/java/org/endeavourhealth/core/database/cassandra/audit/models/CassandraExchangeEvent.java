package org.endeavourhealth.core.database.cassandra.audit.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeEvent;

import java.util.Date;
import java.util.UUID;

@Table(keyspace = "audit", name = "exchange_event")
public class CassandraExchangeEvent {

    @PartitionKey(value = 1)
    @Column(name = "timestamp")
    private Date timestamp = null;
    @PartitionKey(value = 0)
    @Column(name = "exchange_id")
    private UUID exchangeId = null;
    @Column(name = "event_desc")
    private String eventDesc = null;

    public CassandraExchangeEvent() {}

    public CassandraExchangeEvent(ExchangeEvent proxy) {
        this.exchangeId = proxy.getExchangeId();
        this.timestamp = proxy.getTimestamp();
        this.eventDesc = proxy.getEventDesc();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
}
