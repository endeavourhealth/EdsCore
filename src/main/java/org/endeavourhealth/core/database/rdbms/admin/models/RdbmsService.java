package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.core.database.dal.admin.models.Service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "service")
public class RdbmsService implements Serializable {

    private String id = null;
    private String name = null;
    private String localId = null;
    private String endpoints = null; //json containing a map of linked endpoints
    private String publisherConfigName = null; //config name that will tell us where published data is
    private String notes = null;
    private String postcode = null;
    private String ccgCode = null;
    private String organisationType = null;

    public RdbmsService() {}

    public RdbmsService(Service proxy) throws Exception {
        this.id = proxy.getId().toString();
        this.name = proxy.getName();
        this.localId = proxy.getLocalId();
        this.endpoints = proxy.getEndpoints();
        this.publisherConfigName = proxy.getPublisherConfigName();
        this.notes = proxy.getNotes();
        this.postcode = proxy.getPostcode();
        this.ccgCode = proxy.getCcgCode();
        if (proxy.getOrganisationType() != null) {
            this.organisationType = proxy.getOrganisationType().getCode();
        }
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

    @Column(name = "publisher_config_name", nullable = true)
    public String getPublisherConfigName() {
        return publisherConfigName;
    }

    public void setPublisherConfigName(String publisherConfigName) {
        this.publisherConfigName = publisherConfigName;
    }

    @Column(name = "notes", nullable = true)
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "postcode", nullable = true)
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Column(name = "ccg_code", nullable = true)
    public String getCcgCode() {
        return ccgCode;
    }

    public void setCcgCode(String ccgCode) {
        this.ccgCode = ccgCode;
    }

    @Column(name = "organisation_type", nullable = true)
    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }
}
