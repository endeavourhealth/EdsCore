package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "procedure_cds")
public class RdbmsStagingCds {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date cdsActivityDate;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Date dateOfBirth;
    private String consultantCode;
    private Date procedureDate;
    private String procedureOpcsCode;
    private int procedureSeqNbr;
    private String primaryProcedureOpcsCode;
    private String lookupProcedureOpcsTerm;
    private int lookupPersonId ;
    private int lookupConsultantPersonnelId;

    private String auditJson;

    public RdbmsStagingCds() {}

    public RdbmsStagingCds(StagingCds proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDtReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.cdsActivityDate = proxy.getCdsActivityDate();
        this.susRecordType = proxy.getSusRecordType();
        this.cdsUniqueIdentifier = proxy.getCdsUniqueIdentifier();
        this.cdsUpdateType = proxy.getCdsUpdateType();
        this.mrn = proxy.getMrn();
        this.nhsNumber = proxy.getNhsNumber();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.consultantCode = proxy.getConsultantCode();
        this.procedureDate = proxy.getProcedureDate();
        this.procedureOpcsCode = proxy.getProcedureOpcsCode();
        this.procedureSeqNbr = proxy.getProcedureSeqNbr();
        this.primaryProcedureOpcsCode = proxy.getPrimaryProcedureOpcsCode();
        this.lookupProcedureOpcsTerm = proxy.getLookupProcedureOpcsTerm();
        this.lookupPersonId = proxy.getLookupPersonId();
        this.lookupConsultantPersonnelId = proxy.getLookupConsultantPersonnelId();

        if (proxy.getAudit()!= null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "exchange_id")
    public String getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Basic
    @Column(name = "dt_received")
    public Date getDtReceived() {
        return dtReceived;
    }
    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    @Basic
    @Column(name = "record_checksum")
    public int getRecordChecksum() {
        return recordChecksum;
    }
    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    @Basic
    @Column(name="cds_activity_date")
    public Date getCdsActivityDate() {return  this.cdsActivityDate;}
    public void setCdsActivityDate(Date cdsActivityDate) {this.cdsActivityDate=cdsActivityDate;}

    @Basic
    @Column(name = "sus_record_type")
    public String getSusRecordType() {
        return susRecordType;
    }
    public void setSusRecordType(String susRecordType) {
        this.susRecordType = susRecordType;
    }

    @Basic
    @Column(name = "cds_unique_identifier")
    public String getCdsUniqueIdentifier() {
        return cdsUniqueIdentifier;
    }
    public void setCdsUniqueIdentifier(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    @Basic
    @Column(name = "cds_update_type")
    public int getCdsUpdateType() {
        return cdsUpdateType;
    }
    public void setCdsUpdateType(int cdsUpdateType) {
        this.cdsUpdateType = cdsUpdateType;
    }

    @Basic
    @Column(name = "mrn")
    public String getMrn() {
        return mrn;
    }
    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    @Basic
    @Column(name = "nhs_number ")
    public String getNhsNumber () {
        return nhsNumber ;
    }
    public void setNhsNumber (String nhsNumber ) {
        this.nhsNumber  = nhsNumber;
    }

    @Basic
    @Column(name = "date_of_birth")
    public Date getDateOfBirth  () {
        return dateOfBirth ;
    }
    public void setDateOfBirth (Date dateOfBirth ) {
        this.dateOfBirth  = dateOfBirth;
    }

    @Column(name = "consultant_code")
    public String getConsultantCode  () {
        return consultantCode ;
    }
    public void setConsultantCode (String consultantCode ) {
        this.consultantCode = consultantCode;
    }

    @Basic
    @Column(name = "procedure_date")
    public Date getProcedureDate () {
        return procedureDate;
    }
    public void setProcedureDate (Date procedureDate ) {
        this.procedureDate  = procedureDate;
    }

    @Basic
    @Column(name = "procedure_opcs_code")
    public String getProcedureOpcsCode  () {
        return procedureOpcsCode ;
    }
    public void setProcedureOpcsCode (String procedureOpcsCode ) {
        this.procedureOpcsCode = procedureOpcsCode;
    }

    @Basic
    @Column(name = "procedure_seq_nbr")
    public int getProcedureSeqNbr  () {
        return procedureSeqNbr ;
    }
    public void setProcedureSeqNbr (int procedureSeqNbr ) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    @Basic
    @Column(name = "primary_procedure_opcs_code")
    public String getPrimaryProcedureOpcsCode () {
        return primaryProcedureOpcsCode ;
    }
    public void setPrimaryProcedureOpcsCode (String primaryProcedureOpcsCode ) {this.primaryProcedureOpcsCode = primaryProcedureOpcsCode; }

    @Basic
    @Column(name = "lookup_procedure_opcs_term")
    public String getLookupProcedureOpcsTerm () {
        return lookupProcedureOpcsTerm ;
    }
    public void setLookupProcedureOpcsTerm (String lookupProcedureOpcsTerm ) {this.lookupProcedureOpcsTerm = lookupProcedureOpcsTerm; }

    @Basic
    @Column(name = "lookup_person_id")
    public int getLookupPersonId () {
        return lookupPersonId ;
    }
    public void setLookupPersonId (int lookupPersonId ) {this.lookupPersonId = lookupPersonId; }

    @Basic
    @Column(name = "lookup_consultant_personnel_id")
    public int getLookupConsultantPersonnelId () {
        return lookupConsultantPersonnelId ;
    }
    public void setLookupConsultantPersonnelId (int lookupConsultantPersonnelId ) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
    }

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }


//    @Override
//    public int hashCode() {
//
//        return Objects.hash(susRecordType,
//                            cdsUniqueIdentifier,
//                            cdsUpdateType,
//                            mrn,
//                            nhsNumber,
//                            dateOfBirth,
//                            consultantCode,
//                            procedureDate,
//                            procedureOpcsCode,
//                            procedureSeqNbr,
//                            primaryProcedureOpcsCode,
//                            lookupProcedureOpcsTerm,
//                            lookupPersonId,
//                            lookupConsultantPersonnelId);
//    }
}
