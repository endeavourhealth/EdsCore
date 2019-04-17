package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "staging_cds")
public class RdbmsStagingCds {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private String susRecordType;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Date dateOfBirth;
    private Date procedureDate;
    private String procedureOpcsCode;
    private String procedureOpcsTerm;
    private int procedureSeqNbr;
    private String consultantCode;
    private String location;
    private int personId;
    private String auditJson;

    public RdbmsStagingCds() {}

    public RdbmsStagingCds(StagingCds proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDTReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.susRecordType = proxy.getSusRecordType();
        this.cdsUniqueIdentifier = proxy.getCdsUniqueIdentifier();
        this.cdsUpdateType = proxy.getCdsUpdateType();
        this.mrn = proxy.getMrn();
        this.nhsNumber = proxy.getNhsNumber();
        this.dateOfBirth = proxy.getDateOfBirth();
        this.procedureDate = proxy.getProcedureDate();
        this.procedureOpcsCode = proxy.getProcedureOpcsCode();
        this.procedureOpcsTerm = proxy.getProcedureOpcsTerm();
        this.procedureSeqNbr = proxy.getProcedureSeqNbr();
        this.consultantCode = proxy.getConsultantCode();
        this.location = proxy.getLocation();
        this.personId = proxy.getPersonId();
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
    public Date getDTReceived() {
        return dtReceived;
    }
    public void setDTReceived(Date dtReceived) {
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
    public void setCdsUniqueIdentifierm(String cdsUniqueIdentifier) {
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
    @Column(name = "procedure_opcs_term")
    public String getProcedureOpcsTerm  () {
        return procedureOpcsTerm ;
    }
    public void setProcedureOpcsTerm (String procedureOpcsTerm ) {
        this.procedureOpcsTerm = procedureOpcsTerm;
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
    @Column(name = "consultant_code")
    public String getConsultantCode  () {
        return consultantCode ;
    }
    public void setConsultantCode (String consultantCode ) {
        this.consultantCode = consultantCode;
    }

    @Basic
    @Column(name = "location")
    public String getLocation  () {
        return location ;
    }
    public void setLocation (String location ) {
        this.location = location;
    }

    @Basic
    @Column(name = "person_id")
    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }


    @Override
    public int hashCode() {

        return Objects.hash(susRecordType,
                            cdsUniqueIdentifier,
                            cdsUpdateType,
                            mrn,
                            nhsNumber,
                            dateOfBirth,
                            procedureDate,
                            procedureOpcsCode,
                            procedureOpcsTerm,
                            procedureSeqNbr,
                            consultantCode,
                            location,
                            personId);
    }
}
