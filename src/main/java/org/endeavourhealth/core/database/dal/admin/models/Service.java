package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraService;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service {

    private UUID id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private Map<UUID, String> organisations = null;

    public Service() {}

    public Service(CassandraService proxy) {
        this.id = proxy.getId();
        this.name = proxy.getName();
        this.localId = proxy.getLocalIdentifier();
        this.endpoints = proxy.getEndpoints();
        this.organisations = new HashMap<>(proxy.getOrganisations());
    }

    public Service(RdbmsService proxy) throws Exception {
        this.id = UUID.fromString(proxy.getId());
        this.name = proxy.getName();
        this.localId = proxy.getLocalId();
        this.endpoints = proxy.getEndpoints();

        this.organisations = new HashMap<>();
        String json = proxy.getOrganisations();
        Map<String, String> map = ObjectMapperPool.getInstance().readValue(json, HashMap.class);
        for (String key: map.keySet()) {
            UUID uuid = UUID.fromString(key);
            String name = map.get(key);
            this.organisations.put(uuid, name);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String endpoints) {
        this.endpoints = endpoints;
    }

    public Map<UUID, String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Map<UUID, String> organisations) {
        this.organisations = organisations;
    }
}
