package org.endeavourhealth.core.database.dal.admin.models;

import com.fasterxml.jackson.core.type.TypeReference;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.fhir.schema.OrganisationType;
import org.endeavourhealth.core.fhirStorage.ServiceInterfaceEndpoint;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Service {

    private UUID id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private String publisherConfigName = null; //config name pointing to DB storing this services published data
    private String postcode = null;
    private String ccgCode = null;
    private OrganisationType organisationType = null;
    private String alias = null; //AKA
    private Map<String, String> tags = null;

    public Service() {}



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

    public String getPublisherConfigName() {
        return publisherConfigName;
    }

    public void setPublisherConfigName(String publisherConfigName) {
        this.publisherConfigName = publisherConfigName;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "" + id + " " + name + " " + localId;
    }
}
