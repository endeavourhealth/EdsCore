package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingInpatientCds implements Cloneable {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date cdsActivityDate;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Boolean withheld;
    private Date dateOfBirth;
    private String consultantCode;

    private String patientPathwayIdentifier;
    private String spellNumber;
    private String administrativeCategoryCode;
    private String admissionMethodCode;
    private String admissionSourceCode;
    private String patientClassification;
    private Date spellStartDate;
    private String episodeNumber;
    private String episodeStartSiteCode;
    private String episodeStartWardCode;
    private Date episodeStartDate;
    private String episodeEndSiteCode;
    private String episodeEndWardCode;
    private Date episodeEndDate;
    private Date dischargeDate;
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

    private Integer lookupPersonId;
    private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingInpatientCds() {
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

    public Date getCdsActivityDate() {
        return cdsActivityDate;
    }

    public void setCdsActivityDate(Date cdsActivityDate) {
        this.cdsActivityDate = cdsActivityDate;
    }

    public String getCdsUniqueIdentifier() {
        return cdsUniqueIdentifier;
    }

    public void setCdsUniqueIdentifier(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    public int getCdsUpdateType() {
        return cdsUpdateType;
    }

    public void setCdsUpdateType(int cdsUpdateType) {
        this.cdsUpdateType = cdsUpdateType;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public Boolean getWithheld() {
        return withheld;
    }

    public void setWithheld(Boolean withheld) {
        this.withheld = withheld;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getConsultantCode() {
        return consultantCode;
    }

    public void setConsultantCode(String consultantCode) {
        this.consultantCode = consultantCode;
    }

    public String getPatientPathwayIdentifier() {
        return patientPathwayIdentifier;
    }

    public void setPatientPathwayIdentifier(String patientPathwayIdentifier) { this.patientPathwayIdentifier = patientPathwayIdentifier; }

    public String getSpellNumber() {
        return spellNumber;
    }

    public void setSpellNumber(String spellNumber) { this.spellNumber = spellNumber; }

    public String getAdministrativeCategoryCode() {
        return administrativeCategoryCode;
    }

    public void setAdministrativeCategoryCode(String administrativeCategoryCode) {
        this.administrativeCategoryCode = administrativeCategoryCode;
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

    public Date getSpellStartDate() {
        return spellStartDate;
    }

    public void setSpellStartDate(Date spellStartDate) {
        this.spellStartDate = spellStartDate;
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

    public Date getEpisodeStartDate() {
        return episodeStartDate;
    }

    public void setEpisodeStartDate(Date episodeStartDate) {
        this.episodeStartDate = episodeStartDate;
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

    public Date getEpisodeEndDate() {
        return episodeEndDate;
    }

    public void setEpisodeEndDate(Date episodeEndDate) {
        this.episodeEndDate = episodeEndDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
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

    public Integer getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(Integer lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    public Integer getLookupConsultantPersonnelId() {
        return lookupConsultantPersonnelId;
    }

    public void setLookupConsultantPersonnelId(Integer lookupConsultantPersonnelId) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
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

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }


    public StagingInpatientCds clone() throws CloneNotSupportedException {
        return (StagingInpatientCds) super.clone();
    }

    @Override
    public int hashCode() {

        //only calculate the hash from the non-primary key fields
        return Objects.hash(
                cdsActivityDate,
                cdsUpdateType,
                mrn,
                nhsNumber,
                withheld,
                dateOfBirth,
                consultantCode,
                patientPathwayIdentifier,
                spellNumber,
                administrativeCategoryCode,
                admissionMethodCode,
                admissionSourceCode,
                patientClassification,
                spellStartDate,
                episodeNumber,
                episodeStartSiteCode,
                episodeStartWardCode,
                episodeStartDate,
                episodeEndSiteCode,
                episodeEndWardCode,
                episodeEndDate,
                dischargeDate,
                dischargeDestinationCode,
                dischargeMethod,
                maternityDataBirth,
                maternityDataDelivery,
                primaryDiagnosisICD,
                secondaryDiagnosisICD,
                otherDiagnosisICD,
                primaryProcedureOPCS,
                primaryProcedureDate,
                secondaryProcedureOPCS,
                secondaryProcedureDate,
                otherProceduresOPCS,
                lookupPersonId,
                lookupConsultantPersonnelId);
    }

    @Override
    public String toString() {
        return "StagingInpatientCds{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", recordChecksum=" + recordChecksum +
                ", cdsActivityDate=" + cdsActivityDate +
                ", cdsUniqueIdentifier='" + cdsUniqueIdentifier + '\'' +
                ", cdsUpdateType=" + cdsUpdateType +
                ", mrn='" + mrn + '\'' +
                ", nhsNumber='" + nhsNumber + '\'' +
                ", withheld='" + withheld + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", consultantCode='" + consultantCode + '\'' +
                ", patientPathwayIdentifier='" + patientPathwayIdentifier + '\'' +
                ", spellNumber='" + spellNumber + '\'' +
                ", administrativeCategoryCode='" + administrativeCategoryCode + '\'' +
                ", admissionMethodCode='" + admissionMethodCode + '\'' +
                ", admissionSourceCode='" + admissionSourceCode + '\'' +
                ", patientClassification='" + patientClassification + '\'' +
                ", spellStartDate='" + spellStartDate + '\'' +
                ", episodeNumber='" + episodeNumber + '\'' +
                ", episodeStartSiteCode='" + episodeStartSiteCode + '\'' +
                ", episodeStartWardCode='" + episodeStartWardCode + '\'' +
                ", episodeStartDate='" + episodeStartDate + '\'' +
                ", episodeEndSiteCode='" + episodeEndSiteCode + '\'' +
                ", episodeEndWardCode='" + episodeEndWardCode + '\'' +
                ", episodeEndDate='" + episodeEndDate + '\'' +
                ", dischargeDate='" + dischargeDate + '\'' +
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
                ", lookupPersonId=" + lookupPersonId +
                ", lookupConsultantPersonnelId=" + lookupConsultantPersonnelId +
                ", audit=" + audit +
                '}';
    }


}