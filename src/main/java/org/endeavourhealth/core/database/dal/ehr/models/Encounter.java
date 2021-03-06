package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class Encounter {

    private int id;
    private int organizationId;
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

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
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