package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingProcedureTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer performerPersonnelId;
    private Date dtPerformed;
    private Date dtEnded;
    private String freeText;
    private Integer recordedByPersonnelId;
    private Date dtRecorded;
    private String procedureType;
    private String procedureCode;
    private String procedureTerm;
    private Integer procedureSeqNbr;
    private String parentProcedureUniqueId;
    private String qualifier;
    private String location;
    private String specialty;

    private ResourceFieldMappingAudit audit = null;

    public StagingProcedureTarget() {}


    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Integer getPerformerPersonnelId() {
        return performerPersonnelId;
    }

    public void setPerformerPersonnelId(Integer performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    public Date getDtPerformed() {
        return dtPerformed;
    }

    public void setDtPerformed(Date dtPerformed) {
        this.dtPerformed = dtPerformed;
    }

    public Date getDtEnded() {
        return dtEnded;
    }

    public void setDtEnded(Date dtEnded) {
        this.dtEnded = dtEnded;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public Integer getRecordedByPersonnelId() {
        return recordedByPersonnelId;
    }

    public void setRecordedByPersonnelId(Integer recordeByPersonnelId) {
        this.recordedByPersonnelId = recordeByPersonnelId;
    }

    public Date getDtRecorded() {
        return dtRecorded;
    }

    public void setDtRecorded(Date dtRecorded) {
        this.dtRecorded = dtRecorded;
    }

    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
    }

    public Integer getProcedureSeqNbr() {
        return procedureSeqNbr;
    }

    public void setProcedureSeqNbr(Integer procedureSeqNbr) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    public String getParentProcedureUniqueId() {
        return parentProcedureUniqueId;
    }

    public void setParentProcedureUniqueId(String parentProcedureUniqueId) {
        this.parentProcedureUniqueId = parentProcedureUniqueId;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public String toString() {
        return "StagingProcedureTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", dtPerformed=" + dtPerformed +
                ", dtEnded=" + dtEnded +
                ", freeText='" + freeText + '\'' +
                ", recordedByPersonnelId='" + recordedByPersonnelId + '\'' +
                ", dtRecorded=" + dtRecorded +
                ", procedureType='" + procedureType + '\'' +
                ", procedureCode=" + procedureCode +
                ", procedureTerm='" + procedureTerm + '\'' +
                ", procedureSeqNbr=" + procedureSeqNbr +
                ", parentProcedureUniqueId='" + parentProcedureUniqueId + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", location=" + location +
                ", speciality=" + specialty +
                ", audit=" + audit +
                '}';
    }
}