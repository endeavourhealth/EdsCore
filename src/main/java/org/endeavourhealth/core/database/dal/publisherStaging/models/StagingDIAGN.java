package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingDIAGN {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int diagnosisId;
    private boolean activeInd;
    private Integer encounterId;
    private Integer encounterSliceId;
    private Date diagnosisDtTm;
    private String diagnosisCodeType;
    private String diagnosisCode;
    private String diagnosisTerm;
    private String diagnosisNotes;
    private String diagnosisType;
    private Integer diagnosisSeqNo;
    private Integer diagnosisPersonnelId;
    private Integer lookupPersonId;
    private String lookupMrn;
    private ResourceFieldMappingAudit audit = null;

    public StagingDIAGN() {
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

    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosisId) {
        this.diagnosisId = diagnosisId;
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

    public Date getDiagnosisDtTm() {
        return diagnosisDtTm;
    }

    public void setDiagnosisDtTm(Date diagnosisDtTm) {
        this.diagnosisDtTm = diagnosisDtTm;
    }

    public String getDiagnosisCodeType() {
        return diagnosisCodeType;
    }

    public void setDiagnosisCodeType(String diagnosisCodeType) {
        this.diagnosisCodeType = diagnosisCodeType;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisTerm() {
        return diagnosisTerm;
    }

    public void setDiagnosisTerm(String diagnosisTerm) {
        this.diagnosisTerm = diagnosisTerm;
    }

    public String getDiagnosisNotes() {
        return diagnosisNotes;
    }

    public void setDiagnosisNotes(String diagnosisNotes) {
        this.diagnosisNotes = diagnosisNotes;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public Integer getDiagnosisSeqNo() {
        return diagnosisSeqNo;
    }

    public void setDiagnosisSeqNo(Integer diagnosisSeqNo) {
        this.diagnosisSeqNo = diagnosisSeqNo;
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

    public Integer getDiagnosisPersonnelId() {
        return diagnosisPersonnelId;
    }

    public void setDiagnosisPersonnelId(Integer diagnosisPersonnelId) {
        this.diagnosisPersonnelId = diagnosisPersonnelId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        //only hash non primary-key fields
        return Objects.hash(
                            activeInd,
                            encounterId,
                            encounterSliceId,
                            diagnosisDtTm,
                            diagnosisCodeType,
                            diagnosisCode,
                            diagnosisTerm,
                            diagnosisNotes,
                            diagnosisType,
                            diagnosisSeqNo,
                            diagnosisPersonnelId,
                            lookupPersonId,
                            lookupMrn);
    }


    @Override
    public String toString() {
        return "StagingDIAGN{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", recordChecksum=" + recordChecksum +
                ", diagnosisId=" + diagnosisId +
                ", activeInd=" + activeInd +
                ", encounterId=" + encounterId +
                ", encounterSliceId=" + encounterSliceId +
                ", diagnosisDtTm=" + diagnosisDtTm +
                ", diagnosisCodeType='" + diagnosisCodeType + '\'' +
                ", DiagnosisCode='" + diagnosisCode + '\'' +
                ", diagnosisTerm='" + diagnosisTerm + '\'' +
                ", diagnosisNotes='" + diagnosisNotes + '\'' +
                ", diagnosisType='" + diagnosisType + '\'' +
                ", diagnosisSeqNo=" + diagnosisSeqNo +
                ", diagnosisPersonnelId='" + diagnosisPersonnelId + '\'' +
                ", lookupPersonId=" + lookupPersonId +
                ", lookupMrn='" + lookupMrn + '\'' +
                ", audit=" + audit +
                '}';
    }
}