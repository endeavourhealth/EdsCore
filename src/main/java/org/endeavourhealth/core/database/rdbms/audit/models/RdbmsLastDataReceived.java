package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.core.database.dal.audit.models.LastDataReceived;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "last_data_received")
public class RdbmsLastDataReceived implements Serializable {

    private String serviceId;
    private String systemId;
    private Date dataDate;
    private Date receivedDate;
    private String exchangeId;

    public RdbmsLastDataReceived() {
    }

    public RdbmsLastDataReceived(LastDataReceived proxy) {
        this.serviceId = proxy.getServiceId().toString();
        this.systemId = proxy.getSystemId().toString();
        this.dataDate = proxy.getDataDate();
        this.receivedDate = proxy.getReceivedDate();
        this.exchangeId = proxy.getExchangeId().toString();
    }

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "system_id", nullable = false)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Column(name = "data_date", nullable = false)
    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    @Column(name = "received_date", nullable = false)
    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }
}
