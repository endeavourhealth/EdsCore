package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingSURCP;

import java.util.Date;
import java.util.Objects;

public class StagingSURCP {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date csdActivityDate;
    private int surgicalCaseProcedureId;
    private int surgicalCaseId;
    private Date dtExtract;
    private boolean activeInd;
    private int procedureCode;
    private String procedureText;
    private String modifierText;
    private int primaryProcedureIndicator;
    private int surgeonPersonnelId;
    private Date dtStart;
    private Date dtStop;
    private String woundClassCode;
    private ResourceFieldMappingAudit audit = null;

    public StagingSURCP() {}

    public StagingSURCP(RdbmsStagingSURCP proxy) throws Exception {
        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDtReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.csdActivityDate = proxy.getCsdActivityDate();
        this.surgicalCaseProcedureId = proxy.getSurgicalCaseProcedureId();
        this.surgicalCaseId = proxy.getSurgicalCaseId();
        this.dtExtract = proxy.getDTExtract();
        this.activeInd = proxy.getActiveInd();
        this.procedureCode = proxy.getProcedureCode();
        this.procedureText = proxy.getProcedureText();
        this.modifierText = proxy.getModifierText();
        this.primaryProcedureIndicator = proxy.getPrimaryProcedureIndicator();
        this.surgeonPersonnelId = proxy.getSurgeonPersonnelId();
        this.dtStart = proxy.getDTStart();
        this.dtStop = proxy.getDTStop();
        this.woundClassCode = proxy.getWoundClassCode();

        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

    public String getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Date getDTReceived() {
        return dtReceived;
    }
    public void setDTReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    public int getRecordChecksum() {return recordChecksum; }
    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    public Date getDtReceived() {
        return dtReceived;
    }

    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    public Date getCsdActivityDate() {
        return csdActivityDate;
    }

    public void setCsdActivityDate(Date csdActivityDate) {
        this.csdActivityDate = csdActivityDate;
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

    public int getSurgicalCaseProcedureId() { return surgicalCaseProcedureId; }
    public void setSurgicalCaseProcedureId(int surgicalCaseProcedureId) {this.surgicalCaseProcedureId = surgicalCaseProcedureId; }

    public int getSurgicalCaseId() {
        return surgicalCaseId;
    }
    public void setSurgicalCaseId(int surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    public Date getDTExtract() {
        return dtExtract;
    }
    public void setDTExtract(Date dtExtract) { this.dtExtract = dtExtract; }

    public boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public int getProcedureCode  () {
        return procedureCode ;
    }
    public void setProcedureCode (int procedureCode ) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureText () { return procedureText; }
    public void setProcedureText (String procedureText ) {
        this.procedureText = procedureText;
    }

    public String getModifierText () {
        return modifierText;
    }
    public void setModifierText (String modifierText ) {
        this.modifierText = modifierText;
    }

    public int getPrimaryProcedureIndicator () {
        return primaryProcedureIndicator;
    }
    public void setPrimaryProcedureIndicator (int primaryProcedureIndicator) { this.primaryProcedureIndicator = primaryProcedureIndicator; }

    public int getSurgeonPersonnelId () {
        return surgeonPersonnelId;
    }
    public void setSurgeonPersonnelId (int surgeonPersonnelId ) { this.surgeonPersonnelId = surgeonPersonnelId; }

    public Date getDTStart () {
        return dtStart;
    }
    public void setDTStart (Date dtStart) { this.dtStart = dtStart; }

    public Date getDTStop () {
        return dtStop;
    }
    public void setDTStop (Date dtStop) {
        this.dtStop = dtStop;
    }

    public String getWoundClassCode () {
        return woundClassCode;
    }
    public void setWoundClassCode (String woundClassCode) {
        this.woundClassCode = woundClassCode;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }
    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        return Objects.hash(surgicalCaseProcedureId,
                surgicalCaseId,
                dtExtract,
                activeInd,
                procedureCode,
                procedureText,
                modifierText,
                primaryProcedureIndicator,
                surgeonPersonnelId,
                dtStart,
                dtStop,
                woundClassCode);
    }
}