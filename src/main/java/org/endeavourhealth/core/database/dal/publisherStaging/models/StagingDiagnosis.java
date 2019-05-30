package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingDiagnosis {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int diagnosisId;
    private int personId;
    private String mrn;
    private Boolean activeInd;
    private int encounterId;
    private Date diagDtTm;
    private String diagType;
    private String consultant;
    private String vocab;
    private String diagCd;
    private String diagTerm;
    private String notes;
    private String confirmation;
    private String classification;
    private String ranking;
    private String axis;
    private String location;
    private Integer lookupConsultantPersonnelId;

    private ResourceFieldMappingAudit audit = null;

    public StagingDiagnosis() {
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

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public int getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public Date getDiagDtTm() {
        return diagDtTm;
    }

    public void setDiagDtTm(Date diagDtTm) {
        this.diagDtTm = diagDtTm;
    }

    public String getNotes() { return notes; }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVocab() {
        return vocab;
    }

    public void setVocab(String vocab) {
        this.vocab = vocab;
    }

    public String getDiagCd() {
        return diagCd;
    }

    public void setDiagCd(String diagCd) {
        this.diagCd = diagCd;
    }

    public String getDiagTerm() {
        return diagTerm;
    }

    public void setDiagTerm(String diagTerm) {
        this.diagTerm = diagTerm;
    }

    public String getDiagType() { return diagType;}

    public void setDiagType( String diagType) { this.diagType = diagType; }

    public String getConfirmation() { return confirmation;}

    public void setConfirmation( String confirmation) { this.confirmation = confirmation; }

    public String getClassification() { return classification;}

    public void setClassification( String classification) { this.classification = classification; }

    public String getRanking() { return ranking;}

    public void setRanking( String rank) { this.ranking = ranking; }

    public String getAxis() { return axis;}

    public void setAxis( String axis) { this.axis = axis; }

    public String getLocation() { return location;}

    public void setLocation( String location) { this.location = location; }

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
                activeInd,
                mrn,
                encounterId,
                diagDtTm,
                diagType,
                consultant,
                vocab,
                diagCd,
                diagTerm,
                notes,
                classification,
                ranking,
                axis,
                confirmation,
                location,
                lookupConsultantPersonnelId
        );
    }
}