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

    //TODO - add in remaining Condition/Diagnosis Target attributes
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

    //TODO - add in remaining attribute getter and setter methods

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
        return "StagingConditionProcedureTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", dtPerformed=" + dtPerformed +

                //TODO - add in remaining Diagnosis Target attribute refs

                ", audit=" + audit +
                ", is_confidential=" + isConfidential +
                '}';
    }
}