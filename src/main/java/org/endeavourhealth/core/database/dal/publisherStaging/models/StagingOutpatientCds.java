package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingOutpatientCds implements Cloneable {

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

    private String referralSource;
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

    private Integer lookupPersonId;
    private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingOutpatientCds() {
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

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getPatientPathwayIdentifier() {
        return patientPathwayIdentifier;
    }

    public void setPatientPathwayIdentifier(String patientPathwayIdentifier) { this.patientPathwayIdentifier = patientPathwayIdentifier; }

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

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }


    public StagingOutpatientCds clone() throws CloneNotSupportedException {
        return (StagingOutpatientCds) super.clone();
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
                referralSource,
                patientPathwayIdentifier,
                apptAttendanceIdentifier,
                administrativeCategoryCode,
                apptAttendedCode,
                apptOutcomeCode,
                apptDate,
                apptSiteCode,
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
        return "StagingOutpatientCds{" +
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
                ", referralSource='" + referralSource + '\'' +
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
                ", lookupPersonId=" + lookupPersonId +
                ", lookupConsultantPersonnelId=" + lookupConsultantPersonnelId +
                ", audit=" + audit +
                '}';
    }
}