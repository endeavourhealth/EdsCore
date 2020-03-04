package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingInpatientCdsTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer episodeId;
    private Integer performerPersonnelId;

    private String patientPathwayIdentifier;
    private String spellNumber;
    private String admissionMethodCode;
    private String admissionSourceCode;
    private String patientClassification;
    private Date dtSpellStart;
    private String episodeNumber;
    private String episodeStartSiteCode;
    private String episodeStartWardCode;
    private Date dtEpisodeStart;
    private String episodeEndSiteCode;
    private String episodeEndWardCode;
    private Date dtEpisodeEnd;
    private Date dtDischarge;
    private String dischargeDestinationCode;
    private String dischargeMethod;

    private String maternityDataBirth;
    private String maternityDataDelivery;

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

    public StagingInpatientCdsTarget() {}

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

    public String getSpellNumber() {
        return spellNumber;
    }

    public void setSpellNumber(String spellNumber) {
        this.spellNumber = spellNumber;
    }

    public String getAdmissionMethodCode() {
        return admissionMethodCode;
    }

    public void setAdmissionMethodCode(String admissionMethodCode) {
        this.admissionMethodCode = admissionMethodCode;
    }

    public String getAdmissionSourceCode() {
        return admissionSourceCode;
    }

    public void setAdmissionSourceCode(String admissionSourceCode) {
        this.admissionSourceCode = admissionSourceCode;
    }

    public String getPatientClassification() {
        return patientClassification;
    }

    public void setPatientClassification(String patientClassification) {
        this.patientClassification = patientClassification;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getEpisodeStartSiteCode() {
        return episodeStartSiteCode;
    }

    public void setEpisodeStartSiteCode(String episodeStartSiteCode) {
        this.episodeStartSiteCode = episodeStartSiteCode;
    }

    public String getEpisodeStartWardCode() {
        return episodeStartWardCode;
    }

    public void setEpisodeStartWardCode(String episodeStartWardCode) {
        this.episodeStartWardCode = episodeStartWardCode;
    }


    public String getEpisodeEndSiteCode() {
        return episodeEndSiteCode;
    }

    public void setEpisodeEndSiteCode(String episodeEndSiteCode) {
        this.episodeEndSiteCode = episodeEndSiteCode;
    }

    public String getEpisodeEndWardCode() {
        return episodeEndWardCode;
    }

    public void setEpisodeEndWardCode(String episodeEndWardCode) {
        this.episodeEndWardCode = episodeEndWardCode;
    }

    public Date getDtSpellStart() {
        return dtSpellStart;
    }

    public void setDtSpellStart(Date dtSpellStart) {
        this.dtSpellStart = dtSpellStart;
    }

    public Date getDtEpisodeStart() {
        return dtEpisodeStart;
    }

    public void setDtEpisodeStart(Date dtEpisodeStart) {
        this.dtEpisodeStart = dtEpisodeStart;
    }

    public Date getDtEpisodeEnd() {
        return dtEpisodeEnd;
    }

    public void setDtEpisodeEnd(Date dtEpisodeEnd) {
        this.dtEpisodeEnd = dtEpisodeEnd;
    }

    public Date getDtDischarge() {
        return dtDischarge;
    }

    public void setDtDischarge(Date dtDischarge) {
        this.dtDischarge = dtDischarge;
    }


    public String getDischargeDestinationCode() {
        return dischargeDestinationCode;
    }

    public void setDischargeDestinationCode(String dischargeDestinationCode) {
        this.dischargeDestinationCode = dischargeDestinationCode;
    }

    public String getDischargeMethod() {
        return dischargeMethod;
    }

    public void setDischargeMethod(String dischargeMethod) {
        this.dischargeMethod = dischargeMethod;
    }

    public String getMaternityDataBirth() {
        return maternityDataBirth;
    }

    public void setMaternityDataBirth(String maternityDataBirth) {
        this.maternityDataBirth = maternityDataBirth;
    }

    public String getMaternityDataDelivery() {
        return maternityDataDelivery;
    }

    public void setMaternityDataDelivery(String maternityDataDelivery) {
        this.maternityDataDelivery = maternityDataDelivery;
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
        return "StagingInpatientCdsTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", episodeId='" + episodeId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", patientPathwayIdentifier='" + patientPathwayIdentifier + '\'' +
                ", spellNumber='" + spellNumber + '\'' +
                ", admissionMethodCode='" + admissionMethodCode + '\'' +
                ", admissionSourceCode='" + admissionSourceCode + '\'' +
                ", patientClassification='" + patientClassification + '\'' +
                ", dtSpellStart='" + dtSpellStart + '\'' +
                ", episodeNumber='" + episodeNumber + '\'' +
                ", episodeStartSiteCode='" + episodeStartSiteCode + '\'' +
                ", episodeStartWardCode='" + episodeStartWardCode + '\'' +
                ", dtEpisodeStart='" + dtEpisodeStart + '\'' +
                ", episodeEndSiteCode='" + episodeEndSiteCode + '\'' +
                ", episodeEndWardCode='" + episodeEndWardCode + '\'' +
                ", dtEpisodeEnd='" + dtEpisodeEnd + '\'' +
                ", dtDischarge='" + dtDischarge + '\'' +
                ", dischargeDestinationCode='" + dischargeDestinationCode + '\'' +
                ", dischargeMethod='" + dischargeMethod + '\'' +
                ", maternityDataBirth='" + maternityDataBirth + '\'' +
                ", maternityDataDelivery='" + maternityDataDelivery + '\'' +
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