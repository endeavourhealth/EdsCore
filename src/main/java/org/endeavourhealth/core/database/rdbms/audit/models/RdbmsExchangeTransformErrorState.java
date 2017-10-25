package org.endeavourhealth.core.database.rdbms.audit.models;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformErrorState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exchange_transform_error_state", schema = "public")
public class RdbmsExchangeTransformErrorState implements Serializable {

    private String serviceId = null;
    private String systemId = null;
    private String exchangeIdsInError = null;

    public RdbmsExchangeTransformErrorState() {}

    public RdbmsExchangeTransformErrorState(ExchangeTransformErrorState proxy) throws Exception {
        this.serviceId = proxy.getServiceId().toString();
        this.systemId = proxy.getSystemId().toString();

        List<UUID> exchangeIds = proxy.getExchangeIdsInError();
        this.exchangeIdsInError = ObjectMapperPool.getInstance().writeValueAsString(exchangeIds);
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

    @Column(name = "exchange_ids_in_error", nullable = true)
    public String getExchangeIdsInError() {
        return exchangeIdsInError;
    }

    public void setExchangeIdsInError(String exchangeIdsInError) {
        this.exchangeIdsInError = exchangeIdsInError;
    }
}
