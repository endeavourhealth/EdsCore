package org.endeavourhealth.core.database.rdbms.admin.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service", schema = "admin")
public class RdbmsService {
    private String id;
    private String name;
    private String localId;
    private String endpoints;
    private String organisations;
    private String publisherConfigName;
    private String notes;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "local_id")
    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @Basic
    @Column(name = "endpoints")
    public String getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String endpoints) {
        this.endpoints = endpoints;
    }

    @Basic
    @Column(name = "organisations")
    public String getOrganisations() {
        return organisations;
    }

    public void setOrganisations(String organisations) {
        this.organisations = organisations;
    }

    @Basic
    @Column(name = "publisher_config_name")
    public String getPublisherConfigName() {
        return publisherConfigName;
    }

    public void setPublisherConfigName(String publisherConfigName) {
        this.publisherConfigName = publisherConfigName;
    }

    @Basic
    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsService that = (RdbmsService) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(localId, that.localId) &&
                Objects.equals(endpoints, that.endpoints) &&
                Objects.equals(organisations, that.organisations) &&
                Objects.equals(publisherConfigName, that.publisherConfigName) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, localId, endpoints, organisations, publisherConfigName, notes);
    }
}
