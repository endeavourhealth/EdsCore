package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingOutpatientCdsTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer episodeId;
    private Integer performerPersonnelId;

    private String patientPathwayIdentifier;
    private String apptAttendanceIdentifier;
    private String administrativeCategoryCode;
    private String apptAttendedCode;
    private String apptOutcomeCode;
    private Date apptDate;
    private String apptSiteCode;

    private String primaryDiagnosisICD;
    private String secondaryDiagnosisICD;
    private String otherDiagnosisICD;
    private String primaryProcedureOPCS;
    private Date primaryProcedureDate;
    private String secondaryProcedureOPCS;
    private Date secondaryProcedureDate;
    private String otherProceduresOPCS;

    private Boolean isConfidential;
    private ResourceFieldMappingAudit audit = null;

    public StagingOutpatientCdsTarget() {}

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

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public Integer getPerformerPersonnelId() {
        return performerPersonnelId;
    }

    public void setPerformerPersonnelId(Integer performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    public String getPatientPathwayIdentifier() {
        return patientPathwayIdentifier;
    }

    public void setPatientPathwayIdentifier(String patientPathwayIdentifier) {
        this.patientPathwayIdentifier = patientPathwayIdentifier;
    }

    public String getApptAttendanceIdentifier() {
        return apptAttendanceIdentifier;
    }

    public void setApptAttendanceIdentifier(String apptAttendanceIdentifier) {
        this.apptAttendanceIdentifier = apptAttendanceIdentifier;
    }

    public String getAdministrativeCategoryCode() {
        return administrativeCategoryCode;
    }

    public void setAdministrativeCategoryCode(String administrativeCategoryCode) {
        this.administrativeCategoryCode = administrativeCategoryCode;
    }

    public String getApptAttendedCode() {
        return apptAttendedCode;
    }

    public void setApptAttendedCode(String apptAttendedCode) {
        this.apptAttendedCode = apptAttendedCode;
    }

    public String getApptOutcomeCode() {
        return apptOutcomeCode;
    }

    public void setApptOutcomeCode(String apptOutcomeCode) {
        this.apptOutcomeCode = apptOutcomeCode;
    }

    public Date getApptDate() {
        return apptDate;
    }

    public void setApptDate(Date apptDate) {
        this.apptDate = apptDate;
    }

    public String getApptSiteCode() {
        return apptSiteCode;
    }

    public void setApptSiteCode(String apptSiteCode) {
        this.apptSiteCode = apptSiteCode;
    }

    public String getPrimaryDiagnosisICD() {
        return primaryDiagnosisICD;
    }

    public void setPrimaryDiagnosisICD(String primaryDiagnosisICD) {
        this.primaryDiagnosisICD = primaryDiagnosisICD;
    }

    public String getSecondaryDiagnosisICD() {
        return secondaryDiagnosisICD;
    }

    public void setSecondaryDiagnosisICD(String secondaryDiagnosisICD) {
        this.secondaryDiagnosisICD = secondaryDiagnosisICD;
    }

    public String getOtherDiagnosisICD() {
        return otherDiagnosisICD;
    }

    public void setOtherDiagnosisICD(String otherDiagnosisICD) {
        this.otherDiagnosisICD = otherDiagnosisICD;
    }

    public String getPrimaryProcedureOPCS() {
        return primaryProcedureOPCS;
    }

    public void setPrimaryProcedureOPCS(String primaryProcedureOPCS) {
        this.primaryProcedureOPCS = primaryProcedureOPCS;
    }

    public Date getPrimaryProcedureDate() {
        return primaryProcedureDate;
    }

    public void setPrimaryProcedureDate(Date primaryProcedureDate) {
        this.primaryProcedureDate = primaryProcedureDate;
    }

    public String getSecondaryProcedureOPCS() {
        return secondaryProcedureOPCS;
    }

    public void setSecondaryProcedureOPCS(String secondaryProcedureOPCS) {
        this.secondaryProcedureOPCS = secondaryProcedureOPCS;
    }

    public Date getSecondaryProcedureDate() {
        return secondaryProcedureDate;
    }

    public void setSecondaryProcedureDate(Date secondaryProcedureDate) {
        this.secondaryProcedureDate = secondaryProcedureDate;
    }

    public String getOtherProceduresOPCS() {
        return otherProceduresOPCS;
    }

    public void setOtherProceduresOPCS(String otherProceduresOPCS) {
        this.otherProceduresOPCS = otherProceduresOPCS;
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
        return "StagingOutpatientCdsTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", episodeId='" + episodeId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", patientPathwayIdentifier='" + patientPathwayIdentifier + '\'' +
                ", apptAttendanceIdentifier='" + apptAttendanceIdentifier + '\'' +
                ", administrativeCategoryCode='" + administrativeCategoryCode + '\'' +
                ", apptAttendedCode='" + apptAttendedCode + '\'' +
                ", apptOutcomeCode='" + apptOutcomeCode + '\'' +
                ", apptDate='" + apptDate + '\'' +
                ", apptSiteCode='" + apptSiteCode + '\'' +
                ", primaryDiagnosisICD='" + primaryDiagnosisICD + '\'' +
                ", secondaryDiagnosisICD='" + secondaryDiagnosisICD + '\'' +
                ", otherDiagnosisICD='" + otherDiagnosisICD + '\'' +
                ", primaryProcedureOPCS='" + primaryProcedureOPCS + '\'' +
                ", primaryProcedureDate='" + primaryProcedureDate + '\'' +
                ", secondaryProcedureOPCS='" + secondaryProcedureOPCS + '\'' +
                ", secondaryProcedureDate='" + secondaryProcedureDate + '\'' +
                ", otherProceduresOPCS='" + otherProceduresOPCS + '\'' +
                ", audit=" + audit +
                ", isConfidential=" + isConfidential +
                '}';
    }
}