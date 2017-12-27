package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.dal.admin.models.Service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "service")
public class RdbmsService implements Serializable {

    private String id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private String organisations = null; //json containing a map of linked organisations UUIDs and names
    private String publisherConfigName = null; //config name that will tell us where published data is

    public RdbmsService() {}

    public RdbmsService(Service proxy) throws Exception {
        this.id = proxy.getId().toString();
        this.name = proxy.getName();
        this.localId = proxy.getLocalId();
        this.endpoints = proxy.getEndpoints();

        Map<UUID, String> map = proxy.getOrganisations();
        this.organisations = ObjectMapperPool.getInstance().writeValueAsString(map);

        this.publisherConfigName = proxy.getPublisherConfigName();
    }

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

    @Column(name = "local_id", nullable = true)
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

    @Column(name = "publisher_config_name", nullable = true)
    public String getPublisherConfigName() {
        return publisherConfigName;
    }

    public void setPublisherConfigName(String publisherConfigName) {
        this.publisherConfigName = publisherConfigName;
    }
}
