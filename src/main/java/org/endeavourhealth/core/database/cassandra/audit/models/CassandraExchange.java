package org.endeavourhealth.core.database.cassandra.audit.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.audit.models.Exchange;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Table(keyspace = "audit", name = "exchange")
public class CassandraExchange {

    @Column(name = "timestamp")
    private Date timestamp = null;
    @PartitionKey
    @Column(name = "exchange_id")
    private UUID exchangeId = null;
    @Column(name = "headers")
    private String headers = null;
    @Column(name = "body")
    private String body = null;

    public CassandraExchange() {}

    public CassandraExchange(Exchange proxy) throws Exception {
        this.exchangeId = proxy.getId();
        this.timestamp = proxy.getTimestamp();

        Map<String, String> headersMap = proxy.getHeaders();
        this.headers = ObjectMapperPool.getInstance().writeValueAsString(headersMap);

        this.body = proxy.getBody();
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

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
