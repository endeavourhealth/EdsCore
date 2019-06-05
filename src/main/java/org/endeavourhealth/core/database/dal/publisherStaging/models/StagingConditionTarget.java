package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingConditionTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer performerPersonnelId;
    private Date dtPerformed;
    private String conditionCodeType;
    private String conditionCode;
    private String conditionTerm;
    private String conditionType;
    private String freeText;
    private Integer sequenceNumber;
    private String parentConditionUniqueId;
    private String classification;
    private String confirmation;
    private String problemStatus;
    private Date problemStatusDate;
    private String ranking;
    private String axis;
    private String location;

    private Boolean isConfidential;
    private ResourceFieldMappingAudit audit = null;

    public StagingConditionTarget() {}


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

    public String getConditionCodeType() {
        return conditionCodeType;
    }

    public void setConditionCodeType(String conditionCodeType) {
        this.conditionCodeType = conditionCodeType;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getConditionTerm() {
        return conditionTerm;
    }

    public void setConditionTerm(String conditionTerm) {
        this.conditionTerm = conditionTerm;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText (String freeText) {
        this.freeText = freeText;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setParentConditionUniqueId(String parentConditionUniqueId) {
        this.parentConditionUniqueId = parentConditionUniqueId;
    }

    public String getParentConditionUniqueId() {
        return parentConditionUniqueId;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getClassification() {
        return classification;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setProblemStatus(String problemStatus) {
        this.problemStatus = problemStatus;
    }

    public String getProblemStatus() {
        return problemStatus;
    }

    public Date getProblemStatusDate() {
        return problemStatusDate;
    }

    public void setProblemStatusDate(Date problemStatusDate) {
        this.problemStatusDate = problemStatusDate;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getRanking() {
        return ranking;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public String getAxis() {
        return axis;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    public Boolean isConfidential() {
        return isConfidential;
    }

    public void setConfidential(Boolean confidential) {
        isConfidential = confidential;
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
                ", conditionCodeType=" + conditionCodeType +
                ", conditionCode='" + conditionCode + '\'' +
                ", conditionTerm='" + conditionTerm + '\'' +
                ", conditionType='" + conditionType + '\'' +
                ", freeText=" + freeText +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", parentConditionUniqueId=" + parentConditionUniqueId +
                ", classification='" + classification + '\'' +
                ", confirmation='" + confirmation + '\'' +
                ", problemStatus=" + problemStatus +
                ", problemStatusDate=" + problemStatusDate +
                ", ranking='" + ranking + '\'' +
                ", axis='" + axis + '\'' +
                ", location=" + location +
                ", audit=" + audit +
                ", isConfidential=" + isConfidential +
                '}';
    }
}