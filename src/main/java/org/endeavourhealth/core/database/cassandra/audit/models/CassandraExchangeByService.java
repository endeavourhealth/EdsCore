package org.endeavourhealth.core.database.cassandra.audit.models;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.audit.models.Exchange;

import java.util.Date;
import java.util.UUID;

@Table(keyspace = "audit", name = "exchange_by_service")
public class CassandraExchangeByService {

    @PartitionKey
    @Column(name = "service_id")
    private UUID serviceId = null;
    @ClusteringColumn(0)
    @Column(name = "timestamp")
    private Date timestamp = null;
    @Column(name = "exchange_id")
    private UUID exchangeId = null;

    public CassandraExchangeByService() {}

    public CassandraExchangeByService(Exchange proxy) throws Exception {
        this.exchangeId = proxy.getId();
        this.timestamp = proxy.getTimestamp();
        this.serviceId = proxy.getServiceId();
    }


    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
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
}
