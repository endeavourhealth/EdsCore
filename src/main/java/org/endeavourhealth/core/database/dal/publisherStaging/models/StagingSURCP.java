package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingSURCP {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int surgicalCaseProcedureId;
    private Integer surgicalCaseId;
    private Date dtExtract;
    private boolean activeInd;
    private Integer procedureCode;
    private String procedureText;
    private String modifierText;
    private Integer primaryProcedureIndicator;
    private Integer surgeonPersonnelId;
    private Date dtStart;
    private Date dtStop;
    private Integer woundClassCode;
    private String lookupProcedureCodeTerm;
    private ResourceFieldMappingAudit audit = null;
    private String lookupWoundClassTerm;

    public StagingSURCP() {}

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

    public int getSurgicalCaseProcedureId() {
        return surgicalCaseProcedureId;
    }

    public void setSurgicalCaseProcedureId(int surgicalCaseProcedureId) {
        this.surgicalCaseProcedureId = surgicalCaseProcedureId;
    }

    public Integer getSurgicalCaseId() {
        return surgicalCaseId;
    }

    public void setSurgicalCaseId(Integer surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    public Date getDtExtract() {
        return dtExtract;
    }

    public void setDtExtract(Date dtExtract) {
        this.dtExtract = dtExtract;
    }

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public Integer getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(Integer procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureText() {
        return procedureText;
    }

    public void setProcedureText(String procedureText) {
        this.procedureText = procedureText;
    }

    public String getModifierText() {
        return modifierText;
    }

    public void setModifierText(String modifierText) {
        this.modifierText = modifierText;
    }

    public Integer getPrimaryProcedureIndicator() {
        return primaryProcedureIndicator;
    }

    public void setPrimaryProcedureIndicator(Integer primaryProcedureIndicator) {
        this.primaryProcedureIndicator = primaryProcedureIndicator;
    }

    public Integer getSurgeonPersonnelId() {
        return surgeonPersonnelId;
    }

    public void setSurgeonPersonnelId(Integer surgeonPersonnelId) {
        this.surgeonPersonnelId = surgeonPersonnelId;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtStop() {
        return dtStop;
    }

    public void setDtStop(Date dtStop) {
        this.dtStop = dtStop;
    }

    public Integer getWoundClassCode() {
        return woundClassCode;
    }

    public void setWoundClassCode(Integer woundClassCode) {
        this.woundClassCode = woundClassCode;
    }

    public String getLookupProcedureCodeTerm() {
        return lookupProcedureCodeTerm;
    }

    public void setLookupProcedureCodeTerm(String lookupProcedureCodeTerm) {
        this.lookupProcedureCodeTerm = lookupProcedureCodeTerm;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    public String getLookupWoundClassTerm() {
        return lookupWoundClassTerm;
    }

    public void setLookupWoundClassTerm(String lookupWoundClassTerm) {
        this.lookupWoundClassTerm = lookupWoundClassTerm;
    }

    @Override
    public int hashCode() {

        //only hash non-primary key fields
        //note DtExtract is not a primary key field, but is explicitly excluded (and really shouldn't be on this table anyway)
        return Objects.hash(
                surgicalCaseId,
                activeInd,
                procedureCode,
                procedureText,
                modifierText,
                primaryProcedureIndicator,
                surgeonPersonnelId,
                dtStart,
                dtStop,
                woundClassCode,
                lookupProcedureCodeTerm,
                lookupWoundClassTerm);
    }

    @Override
    public String toString() {
        return "StagingSURCP{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", recordChecksum=" + recordChecksum +
                ", surgicalCaseProcedureId=" + surgicalCaseProcedureId +
                ", surgicalCaseId=" + surgicalCaseId +
                ", dtExtract=" + dtExtract +
                ", activeInd=" + activeInd +
                ", procedureCode=" + procedureCode +
                ", procedureText='" + procedureText + '\'' +
                ", modifierText='" + modifierText + '\'' +
                ", primaryProcedureIndicator=" + primaryProcedureIndicator +
                ", surgeonPersonnelId=" + surgeonPersonnelId +
                ", dtStart=" + dtStart +
                ", dtStop=" + dtStop +
                ", woundClassCode='" + woundClassCode + '\'' +
                ", lookupProcedureCodeTerm='" + lookupProcedureCodeTerm + '\'' +
                ", audit=" + audit +
                ", lookupWoundClassTerm='" + lookupWoundClassTerm + '\'' +
                '}';
    }
}