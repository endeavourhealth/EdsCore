package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingPROCE;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "staging_procedure")
public class RdbmsStagingPROCE implements Serializable {
    private String exchangeId;
    private Date dateReceived;
    private int checkSum;
    private int procedureId;
    private boolean activeInd;
    private int encounterId;
    private Date procedureDtTm;
    private String procedureType;
    private String procedureCode;
    private String procedureTerm;
    private int procedureSeqNo;
    private int lookupPersonId;
    private String lookupMrn;
    private String lookupNhsNumber;
    private Date lookupDateOfBirth;
    private ResourceFieldMappingAudit audit = null;


    public RdbmsStagingPROCE(StagingPROCE in) {
        this.exchangeId = in.getExchangeId();
        this.dateReceived = in.getDateReceived();
        this.checkSum=in.getCheckSum();
        this.procedureId=in.getProcedureId();
        this.activeInd=in.isActiveInd();
        this.encounterId = in.getEncounterId();
        this.procedureDtTm=in.getProcedureDtTm();
        this.procedureType=in.getProcedureType();
        this.procedureCode = in.getProcedureCode();
        this.procedureTerm = in.getProcedureTerm();
        this.procedureSeqNo=in.getProcedureSeqNo();
        this.lookupPersonId = in.getLookupPersonId();
        this.lookupMrn=in.getLookupMrn();
        this.lookupNhsNumber=in.getLookupNhsNumber();
        this.lookupDateOfBirth=in.getLookupDateOfBirth();
        this.checkSum = in.getCheckSum();
        //  this.auditJson = in.getAuditJson();
    }

    @Id
    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Column(name="dt_received")
    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    @Column(name="record_checksum")
    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    @Column(name="proceedure_id")
    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    @Column(name="active_ind")
    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    @Column(name="encounter_id")
    public int getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
    }

    @Column(name="procedure_dt_tm")
    public Date getProcedureDtTm() {
        return procedureDtTm;
    }

    public void setProcedureDtTm(Date procedureDtTm) {
        this.procedureDtTm = procedureDtTm;
    }

    @Column(name="procedure_type")
    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    @Column(name="procedure_code")
    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    @Column(name="procedure_term")
    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
    }

    @Column(name="procedure_seq")
    public int getProcedureSeqNo() {
        return procedureSeqNo;
    }

    public void setProcedureSeqNo(int procedureSeqNo) {
        this.procedureSeqNo = procedureSeqNo;
    }

    @Column(name="lookup_person_id")
    public int getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(int lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    @Column(name="lookup_mrn")
    public String getLookupMrn() {
        return lookupMrn;
    }

    public void setLookupMrn(String lookupMrn) {
        this.lookupMrn = lookupMrn;
    }

    @Column(name="lookup_nhs")
    public String getLookupNhsNumber() {
        return lookupNhsNumber;
    }

    public void setLookupNhsNumber(String lookupNhsNumber) {
        this.lookupNhsNumber = lookupNhsNumber;
    }

    public Date getLookupDateOfBirth() {
        return lookupDateOfBirth;
    }

    public void setLookupDateOfBirth(Date lookupDateOfBirth) {
        this.lookupDateOfBirth = lookupDateOfBirth;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
