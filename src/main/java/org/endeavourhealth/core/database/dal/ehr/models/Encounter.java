package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class Encounter {

    private int id;
    private int organisationId;
    private int patientId;
    private Date clinicalEffectiveDate;
    private int typeId;
    private int parentEncounterId;
    private String additionalData;

    public Encounter() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(int organisationId) {
        this.organisationId = organisationId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Date getClinicalEffectiveDate() {
        return clinicalEffectiveDate;
    }

    public void setClinicalEffectiveDate(Date clinicalEffectiveDate) {
        this.clinicalEffectiveDate = clinicalEffectiveDate;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getParentEncounterId() {
        return parentEncounterId;
    }

    public void setParentEncounterId(int parentEncounterId) {
        this.parentEncounterId = parentEncounterId;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}