package org.endeavourhealth.core.rdbms.admin.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.cache.ObjectMapperPool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "organisation", schema = "public", catalog = "admin")
public class Organisation  implements Serializable {

    private String id = null;
    private String name = null;
    private String national_id = null;
    private String services = null; //json containing a map of linked service UUIDs and names

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "national_id", nullable = false)
    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    @Column(name = "services", nullable = false)
    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    /**
     * helper to translate the JSON column
     */
    public Map<UUID, String> getServicesMap() throws Exception {
        Map<UUID, String> ret = new HashMap<>();

        JsonNode json = ObjectMapperPool.getInstance().readTree(services);
        Iterator<String> it = json.fieldNames();
        while (it.hasNext()) {
            String fieldName = it.next();
            UUID uuid = UUID.fromString(fieldName);
            JsonNode node = json.get(fieldName);
            String nodeValue = node.asText();

            ret.put(uuid, nodeValue);
        }

        return ret;
    }

    public void setServicesMap(Map<UUID, String> servicesMap) throws Exception {
        this.services = ObjectMapperPool.getInstance().writeValueAsString(servicesMap);
    }

}
