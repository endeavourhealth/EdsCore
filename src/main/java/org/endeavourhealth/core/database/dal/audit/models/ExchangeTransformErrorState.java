package org.endeavourhealth.core.database.dal.audit.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeTransformErrorState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExchangeTransformErrorState {

    private UUID serviceId = null;
    private UUID systemId = null;
    private List<UUID> exchangeIdsInError = null;

    public ExchangeTransformErrorState() {}

    /*public ExchangeTransformErrorState(CassandraExchangeTransformErrorState proxy) {
        this.serviceId = proxy.getServiceId();
        this.systemId = proxy.getSystemId();
        this.exchangeIdsInError = proxy.getExchangeIdsInError();
    }*/

    public ExchangeTransformErrorState(RdbmsExchangeTransformErrorState proxy) throws Exception {
        this.serviceId = UUID.fromString(proxy.getServiceId());
        this.systemId = UUID.fromString(proxy.getSystemId());
        this.exchangeIdsInError = new ArrayList<>();

        ArrayNode json = (ArrayNode)ObjectMapperPool.getInstance().readTree(proxy.getExchangeIdsInError());
        for (int i=0; i<json.size(); i++) {
            JsonNode child = json.get(i);
            String uuidStr = child.asText();
            this.exchangeIdsInError.add(UUID.fromString(uuidStr));
        }
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public List<UUID> getExchangeIdsInError() {
        return exchangeIdsInError;
    }

    public void setExchangeIdsInError(List<UUID> exchangeIdsInError) {
        this.exchangeIdsInError = exchangeIdsInError;
    }
}
