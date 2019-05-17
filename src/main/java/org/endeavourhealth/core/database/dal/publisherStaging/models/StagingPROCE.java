package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingPROCE {

    private String exchangeId;
    private Date dtReceived;
    private int checkSum;
    private int procedureId;
    private boolean activeInd;
    private Integer encounterId;
    private Integer encounterSliceId;
    private Date procedureDtTm;
    private String procedureType;
    private String procedureCode;
    private String procedureTerm;
    private Integer procedureSeqNo;
    private Integer lookupPersonId;
    private String lookupMrn;
    private String lookupNhsNumber;
    private Date lookupDateOfBirth;
    private ResourceFieldMappingAudit audit = null;

    public StagingPROCE() {
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

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Integer getEncounterSliceId() {
        return encounterSliceId;
    }

    public void setEncounterSliceId(Integer encounterSliceId) {
        this.encounterSliceId = encounterSliceId;
    }

    public Date getProcedureDtTm() {
        return procedureDtTm;
    }

    public void setProcedureDtTm(Date procedureDtTm) {
        this.procedureDtTm = procedureDtTm;
    }

    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
    }

    public Integer getProcedureSeqNo() {
        return procedureSeqNo;
    }

    public void setProcedureSeqNo(Integer procedureSeqNo) {
        this.procedureSeqNo = procedureSeqNo;
    }

    public Integer getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(Integer lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    public String getLookupMrn() {
        return lookupMrn;
    }

    public void setLookupMrn(String lookupMrn) {
        this.lookupMrn = lookupMrn;
    }

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

    @Override
    public int hashCode() {

        return Objects.hash(procedureId,
                            activeInd,
                            encounterId,
                            encounterSliceId,
                            procedureDtTm,
                            procedureType,
                            procedureCode,
                            procedureTerm,
                            procedureSeqNo,
                            lookupPersonId,
                            lookupMrn,
                            lookupNhsNumber,
                            lookupDateOfBirth);
    }

    @Override
    public String toString() {
        return "StagingPROCE{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", checkSum=" + checkSum +
                ", procedureId=" + procedureId +
                ", activeInd=" + activeInd +
                ", encounterId=" + encounterId +
                ", encounterSliceId=" + encounterSliceId +
                ", procedureDtTm=" + procedureDtTm +
                ", procedureType='" + procedureType + '\'' +
                ", procedureCode='" + procedureCode + '\'' +
                ", procedureTerm='" + procedureTerm + '\'' +
                ", procedureSeqNo=" + procedureSeqNo +
                ", lookupPersonId=" + lookupPersonId +
                ", lookupMrn='" + lookupMrn + '\'' +
                ", lookupNhsNumber='" + lookupNhsNumber + '\'' +
                ", lookupDateOfBirth=" + lookupDateOfBirth +
                ", audit=" + audit +
                '}';
    }
}


