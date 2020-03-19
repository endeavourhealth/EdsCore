package org.endeavourhealth.core.database.dal.ehr.models;

import java.util.Date;

public class Observation {

    private int id;
    private int organizationId;
    private int patientId;
    private Date clinicalEffectiveDate;
    private int typeId;
    private Double resultValue;
    private Integer encounterId;
    private Integer encounterSectionId;
    private Integer parentObservationId;
    private String additionalData;

    public Observation() {
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

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public Double getResultValue() {
        return resultValue;
    }

    public void setResultValue(Double resultValue) {
        this.resultValue = resultValue;
    }

    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Integer getEncounterSectionId() {
        return encounterSectionId;
    }

    public void setEncounterSectionId(Integer encounterSectionId) {
        this.encounterSectionId = encounterSectionId;
    }

    public Integer getParentObservationId() {
        return parentObservationId;
    }

    public void setParentObservationId(Integer parentObservationId) {
        this.parentObservationId = parentObservationId;
    }
}