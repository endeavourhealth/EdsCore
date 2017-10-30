package org.endeavourhealth.core.database.dal.admin.models;

import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.cassandra.admin.models.CassandraOrganisation;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsOrganisation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Organisation {

    private UUID id = null;
    private String name = null;
    private String nationalId = null;
    private Map<UUID, String> services = null;

    public Organisation() {}

    public Organisation(CassandraOrganisation proxy) {
        this.id = proxy.getId();
        this.name = proxy.getName();
        this.nationalId = proxy.getNationalId();
        this.services = new HashMap<>(proxy.getServices());
    }

    public Organisation(RdbmsOrganisation proxy) throws Exception {
        this.id = UUID.fromString(proxy.getId());
        this.name = proxy.getName();
        this.nationalId = proxy.getNationalId();

        this.services = new HashMap<>();
        String json = proxy.getServices();
        if (!Strings.isNullOrEmpty(json)) {
            Map<String, String> map = ObjectMapperPool.getInstance().readValue(json, HashMap.class);
            for (String key : map.keySet()) {
                UUID uuid = UUID.fromString(key);
                String name = map.get(key);
                this.services.put(uuid, name);
            }
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

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public Map<UUID, String> getServices() {
        return services;
    }

    public void setServices(Map<UUID, String> services) {
        this.services = services;
    }
}
