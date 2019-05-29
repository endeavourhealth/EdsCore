package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingConditionCds implements Cloneable {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date cdsActivityDate;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Boolean withheld;
    private Date dateOfBirth;
    private String consultantCode;

    private String diagnosisIcdCode;
    private int diagnosisSeqNbr;
    private String primaryDiagnosisIcdCode;
    private String lookupDiagnosisIcdTerm;

    private Integer lookupPersonId;
    private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingConditionCds() {
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

    public String getSusRecordType() {
        return susRecordType;
    }

    public void setSusRecordType(String susRecordType) {
        this.susRecordType = susRecordType;
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

    public String getDiagnosisIcdCode() {
        return diagnosisIcdCode;
    }

    public void setDiagnosisIcdCode(String diagnosisIcdCode) { this.diagnosisIcdCode = diagnosisIcdCode; }

    public int getDiagnosisSeqNbr() {
        return diagnosisSeqNbr;
    }

    public void setDiagnosisSeqNbr(int diagnosisSeqNbr) {
        this.diagnosisSeqNbr = diagnosisSeqNbr;
    }

    public String getPrimaryDiagnosisIcdCode() {
        return primaryDiagnosisIcdCode;
    }

    public void setPrimaryDiagnosisIcdCodee(String primaryDiagnosisIcdCode) {
        this.primaryDiagnosisIcdCode = primaryDiagnosisIcdCode;
    }

    public String getLookupDiagnosisIcdTerm() {
        return lookupDiagnosisIcdTerm;
    }

    public void setLookupDiagnosisIcdTerm(String lookupDiagnosisIcdTerm) {
        this.lookupDiagnosisIcdTerm = lookupDiagnosisIcdTerm;
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



    public StagingConditionCds clone() throws CloneNotSupportedException {
        return (StagingConditionCds) super.clone();
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
                diagnosisIcdCode,
                primaryDiagnosisIcdCode,
                lookupDiagnosisIcdTerm,
                lookupPersonId,
                lookupConsultantPersonnelId);
    }


    @Override
    public String toString() {
        return "StagingCds{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", recordChecksum=" + recordChecksum +
                ", cdsActivityDate=" + cdsActivityDate +
                ", susRecordType='" + susRecordType + '\'' +
                ", cdsUniqueIdentifier='" + cdsUniqueIdentifier + '\'' +
                ", cdsUpdateType=" + cdsUpdateType +
                ", mrn='" + mrn + '\'' +
                ", nhsNumber='" + nhsNumber + '\'' +
                ", withheld='" + withheld + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", consultantCode='" + consultantCode + '\'' +
                ", diagnosisIcdCode='" + diagnosisIcdCode + '\'' +
                ", diagnosisSeqNbr=" + diagnosisSeqNbr +
                ", primaryDiagnosisIcdCode='" + primaryDiagnosisIcdCode + '\'' +
                ", lookupDiagnosisIcdTerm='" + lookupDiagnosisIcdTerm + '\'' +
                ", lookupPersonId=" + lookupPersonId +
                ", lookupConsultantPersonnelId=" + lookupConsultantPersonnelId +
                ", audit=" + audit +
                '}';
    }
}