package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingCds implements Cloneable {

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
    private Date procedureDate;
    private String procedureOpcsCode;
    private int procedureSeqNbr;
    private String primaryProcedureOpcsCode;
    private String lookupProcedureOpcsTerm;
    private Integer lookupPersonId;
    private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingCds() {
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

    public Date getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(Date procedureDate) {
        this.procedureDate = procedureDate;
    }

    public String getProcedureOpcsCode() {
        return procedureOpcsCode;
    }

    public void setProcedureOpcsCode(String procedureOpcsCode) {
        this.procedureOpcsCode = procedureOpcsCode;
    }

    public int getProcedureSeqNbr() {
        return procedureSeqNbr;
    }

    public void setProcedureSeqNbr(int procedureSeqNbr) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    public String getPrimaryProcedureOpcsCode() {
        return primaryProcedureOpcsCode;
    }

    public void setPrimaryProcedureOpcsCode(String primaryProcedureOpcsCode) {
        this.primaryProcedureOpcsCode = primaryProcedureOpcsCode;
    }

    public String getLookupProcedureOpcsTerm() {
        return lookupProcedureOpcsTerm;
    }

    public void setLookupProcedureOpcsTerm(String lookupProcedureOpcsTerm) {
        this.lookupProcedureOpcsTerm = lookupProcedureOpcsTerm;
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



    public StagingCds clone() throws CloneNotSupportedException {
        return (StagingCds) super.clone();
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
                procedureDate,
                procedureOpcsCode,
                primaryProcedureOpcsCode,
                lookupProcedureOpcsTerm,
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
                ", procedureDate=" + procedureDate +
                ", procedureOpcsCode='" + procedureOpcsCode + '\'' +
                ", procedureSeqNbr=" + procedureSeqNbr +
                ", primaryProcedureOpcsCode='" + primaryProcedureOpcsCode + '\'' +
                ", lookupProcedureOpcsTerm='" + lookupProcedureOpcsTerm + '\'' +
                ", lookupPersonId=" + lookupPersonId +
                ", lookupConsultantPersonnelId=" + lookupConsultantPersonnelId +
                ", audit=" + audit +
                '}';
    }
}