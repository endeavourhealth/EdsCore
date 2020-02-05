package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class Encounter {

    private String encounterId;
    private String patientId;
    private String practitionerId;
    private String appointmentId;
    private Date effectiveDate;
    private Date effectiveEndDate;
    private String episodeOfCareId;
    private String serviceProviderOrganisationId;
    private String encounterType;
    private String parentEncounterId;
    private String additionalFieldsJson;

    public Encounter() {
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPractitionerId() {
        return practitionerId;
    }

    public void setPractitionerId(String practitionerId) {
        this.practitionerId = practitionerId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getEpisodeOfCareId() {
        return episodeOfCareId;
    }

    public void setEpisodeOfCareId(String episodeOfCareId) {
        this.episodeOfCareId = episodeOfCareId;
    }

    public String getServiceProviderOrganisationId() {
        return serviceProviderOrganisationId;
    }

    public void setServiceProviderOrganisationId(String serviceProviderOrganisationId) {
        this.serviceProviderOrganisationId = serviceProviderOrganisationId;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getParentEncounterId() {
        return parentEncounterId;
    }

    public void setParentEncounterId(String parentEncounterId) {
        this.parentEncounterId = parentEncounterId;
    }

    public String getAdditionalFieldsJson() {
        return additionalFieldsJson;
    }

    public void setAdditionalFieldsJson(String additionalFieldsJson) {
        this.additionalFieldsJson = additionalFieldsJson;
    }
}