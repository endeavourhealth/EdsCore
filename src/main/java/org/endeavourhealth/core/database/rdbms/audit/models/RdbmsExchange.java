package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.audit.models.Exchange;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "exchange", schema = "public")
public class RdbmsExchange implements Serializable {

    private String id = null;
    private Date timestamp = null;
    private String headers = null; //JSON structure of the header map
    private String serviceId = null;
    private String body = null;

    public RdbmsExchange() {}

    public RdbmsExchange(Exchange proxy) throws Exception {
        this.id = proxy.getId().toString();
        this.timestamp = proxy.getTimestamp();

        Map<String, String> headersMap = proxy.getHeaders();
        this.headers = ObjectMapperPool.getInstance().writeValueAsString(headersMap);

        this.serviceId = proxy.getServiceId().toString();
        this.body = proxy.getBody();
    }

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "timestamp", nullable = false)
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "headers", nullable = false)
    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "body", nullable = false)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
