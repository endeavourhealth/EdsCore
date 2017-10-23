package org.endeavourhealth.core.rdbms.admin.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "service", schema = "public", catalog = "admin")
public class Service {

    private String id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private String organisations = null; //json containing a map of linked organisations UUIDs and names

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

    @Column(name = "local_id", nullable = false)
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @Column(name = "endpoints", nullable = true)
    public String getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String endpoints) {
        this.endpoints = endpoints;
    }

    @Column(name = "organisations", nullable = true)
    public String getOrganisations() {
        return organisations;
    }

    public void setOrganisations(String organisations) {
        this.organisations = organisations;
    }

    /**
     * helper to translate the JSON column
     */
    public Map<UUID, String> getOrganisationsMap() throws Exception {
        Map<UUID, String> ret = new HashMap<>();

        JsonNode json = ObjectMapperPool.getInstance().readTree(organisations);
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

    public void setOrganisationsMap(Map<UUID, String> organisationsMap) throws Exception {
        this.organisations = ObjectMapperPool.getInstance().writeValueAsString(organisationsMap);
    }
}
