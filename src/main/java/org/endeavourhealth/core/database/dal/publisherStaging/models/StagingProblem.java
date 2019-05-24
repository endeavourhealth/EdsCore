package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingProblem {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int problemId;
    private int personId;
    private String mrn;
    private Date onsetDtTm;
    private String updatedBy;
    private String problemCd;
    private String problemTerm;
    private String problemTxt;
    private String qualifier;
    private String classification;
    private String confirmation;
    private String persistence;
    private String prognosis;
    private String vocab;
    private String status;
    private Date statusDtTm;
    private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingProblem() {
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getDtReceived() {
        return dtReceived;
    }

    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    public int getRecordChecksum() {
        return recordChecksum;
    }

    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getOnsetDtTm() {
        return onsetDtTm;
    }

    public void setOnsetDtTm(Date onsetDtTm) {
        this.onsetDtTm = onsetDtTm;
    }

    public String getProblemTxt() { return problemTxt; }

    public void setProblemTxt(String problemTxt) {
        this.problemTxt = problemTxt;
    }

    public String getVocab() {
        return vocab;
    }

    public void setVocab(String vocab) {
        this.vocab = vocab;
    }

    public String getProblemCd() {
        return problemCd;
    }

    public void setProblemCd(String problemCd) {
        this.problemCd = problemCd;
    }

    public String getProblemTerm() {
        return problemTerm;
    }

    public void setProblemTerm(String problemTerm) {
        this.problemTerm = problemTerm;
    }

    public String getClassification() { return classification;}

    public void setClassification(String classification) { this.classification = classification; }

    public String getConfirmation() { return confirmation;}

    public void setConfirmation(String confirmation) { this.confirmation = confirmation; }

    public String getPersistence() { return persistence;}

    public void setPersistence(String persistence) { this.persistence = persistence; }

    public String getQualifier() { return qualifier;}

    public void setQualifier(String qualifier) { this.qualifier = qualifier; }

    public String getPrognosis() { return prognosis;}

    public void setPrognosis(String prognosis) { this.prognosis = prognosis; }

    public String getStatus() { return status;}

    public void setStatus( String status) { this.status = status; }

    public Date getStatusDtTm() { return statusDtTm;}

    public void setStatusDtTm(Date statusDtTm) { this.statusDtTm = statusDtTm; }

    public Integer getLookupConsultantPersonnelId() {
        return lookupConsultantPersonnelId;
    }

    public void setLookupConsultantPersonnelId(Integer lookupConsultantPersonnelId) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        //only hash non-primary key fields
        return Objects.hash(
                personId,
                mrn,
                onsetDtTm,
                updatedBy,
                problemCd,
                problemTerm,
                problemTxt,
                qualifier,
                classification,
                confirmation,
                persistence,
                prognosis,
                vocab,
                status,
                statusDtTm,
                lookupConsultantPersonnelId
        );
    }
}
