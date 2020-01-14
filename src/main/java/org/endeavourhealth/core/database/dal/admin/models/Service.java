package org.endeavourhealth.core.database.dal.admin.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.fhir.schema.OrganisationType;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.endeavourhealth.core.fhirStorage.ServiceInterfaceEndpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Service {

    private UUID id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private Map<UUID, String> organisations = null;
    private String publisherConfigName = null; //config name pointing to DB storing this services published data
    private String notes = null;
    private String postcode = null;
    private String ccgCode = null;
    private OrganisationType organisationType = null;

    public Service() {}

    /*public Service(CassandraService proxy) {
        this.id = proxy.getId();
        this.name = proxy.getName();
        this.localId = proxy.getLocalIdentifier();
        this.endpoints = proxy.getEndpoints();
        this.organisations = new HashMap<>(proxy.getOrganisations());
    }*/

    public Service(RdbmsService proxy) throws Exception {
        this.id = UUID.fromString(proxy.getId());
        this.name = proxy.getName();
        this.localId = proxy.getLocalId();
        this.endpoints = proxy.getEndpoints();

        this.organisations = new HashMap<>();
        String json = proxy.getOrganisations();
        if (!Strings.isNullOrEmpty(json)) {
            Map<String, String> map = ObjectMapperPool.getInstance().readValue(json, HashMap.class);
            for (String key : map.keySet()) {
                UUID uuid = UUID.fromString(key);
                String name = map.get(key);
                this.organisations.put(uuid, name);
            }
        }

        this.publisherConfigName = proxy.getPublisherConfigName();
        this.notes = proxy.getNotes();
        this.postcode = proxy.getPostcode();
        this.ccgCode = proxy.getCcgCode();
        if (proxy.getOrganisationType() != null) {
            this.organisationType = OrganisationType.fromCode(proxy.getOrganisationType());
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

    public String getPublisherConfigName() {
        return publisherConfigName;
    }

    public void setPublisherConfigName(String publisherConfigName) {
        this.publisherConfigName = publisherConfigName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCcgCode() {
        return ccgCode;
    }

    public void setCcgCode(String ccgCode) {
        this.ccgCode = ccgCode;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public List<ServiceInterfaceEndpoint> getEndpointsList() throws Exception {
        return ObjectMapperPool.getInstance().readValue(this.endpoints, new TypeReference<List<ServiceInterfaceEndpoint>>() {});
    }

    public void setEndpointsList(List<ServiceInterfaceEndpoint> list) throws Exception {
        this.endpoints = ObjectMapperPool.getInstance().writeValueAsString(list);
    }

    @Override
    public String toString() {
        return "" + id + " " + name + " " + localId;
    }
}
