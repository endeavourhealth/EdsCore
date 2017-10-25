package org.endeavourhealth.core.rdbms.audit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "exchange_transform_error_state", schema = "public", catalog = "audit")
public class ExchangeTransformErrorState  implements Serializable {

    private String serviceId = null;
    private String systemId = null;
    private String exchangeIdsInError = null;

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

    @Column(name = "exchange_ids_in_error", nullable = true)
    public String getExchangeIdsInError() {
        return exchangeIdsInError;
    }

    public void setExchangeIdsInError(String exchangeIdsInError) {
        this.exchangeIdsInError = exchangeIdsInError;
    }
}
