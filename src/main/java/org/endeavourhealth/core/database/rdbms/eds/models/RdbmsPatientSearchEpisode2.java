package org.endeavourhealth.core.database.rdbms.eds.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "patient_search_episode_2")
public class RdbmsPatientSearchEpisode2 implements Serializable {

    private String serviceId = null;
    private String patientId = null;
    private String episodeId = null;
    private Date registrationStart = null;
    private Date registrationEnd = null;
    private String careManager = null;
    private String organisationName = null;
    private String organisationTypeCode = null;
    private String registrationTypeCode = null;
    private Date lastUpdated = null;

    public RdbmsPatientSearchEpisode2() {}

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "patient_id", nullable = false)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Id
    @Column(name = "episode_id", nullable = false)
    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    @Column(name = "registration_start", nullable = true)
    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    @Column(name = "registration_end", nullable = true)
    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    @Column(name = "care_mananger", nullable = true)
    public String getCareManager() {
        return careManager;
    }

    public void setCareManager(String careManager) {
        this.careManager = careManager;
    }

    @Column(name = "organisation_name", nullable = true)
    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    @Column(name = "organisation_type_code", nullable = true)
    public String getOrganisationTypeCode() {
        return organisationTypeCode;
    }

    public void setOrganisationTypeCode(String organisationTypeCode) {
        this.organisationTypeCode = organisationTypeCode;
    }

    @Column(name = "registration_type_code", nullable = true)
    public String getRegistrationTypeCode() {
        return registrationTypeCode;
    }

    public void setRegistrationTypeCode(String registrationTypeCode) {
        this.registrationTypeCode = registrationTypeCode;
    }

    @Column(name = "last_updated", nullable = false)
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
