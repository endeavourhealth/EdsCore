package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_SURCP")
public class RdbmsStagingSURCP {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
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
    private String auditJson;

    public RdbmsStagingSURCP() {}

    public RdbmsStagingSURCP(StagingSURCP proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDTReceived();
        this.recordChecksum = proxy.getRecordChecksum();
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
    @Column(name = "surgical_case_procedure_id")
    public int getSurgicalCaseProcedureId() { return surgicalCaseProcedureId; }
    public void setSurgicalCaseProcedureId(int surgicalCaseProcedureId) {this.surgicalCaseProcedureId = surgicalCaseProcedureId; }

    @Basic
    @Column(name = "surgical_case_id")
    public int getSurgicalCaseId() {
        return surgicalCaseId;
    }
    public void setSurgicalCaseId(int surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    @Basic
    @Column(name = "dt_extract")
    public Date getDTExtract() {
        return dtExtract;
    }
    public void setDTExtract(Date dtExtract) { this.dtExtract = dtExtract; }

    @Basic
    @Column(name = "active_ind")
    public boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    @Basic
    @Column(name = "procedure_code")
    public int getProcedureCode  () {
        return procedureCode ;
    }
    public void setProcedureCode (int procedureCode ) {
        this.procedureCode = procedureCode;
    }

    @Basic
    @Column(name = "procedure_text ")
    public String getProcedureText () { return procedureText; }
    public void setProcedureText (String procedureText ) {
        this.procedureText = procedureText;
    }

    @Basic
    @Column(name = "modifier_text")
    public String getModifierText () {
        return modifierText;
    }
    public void setModifierText (String modifierText ) {
        this.modifierText = modifierText;
    }

    @Basic
    @Column(name = "primary_procedure_indicator")
    public int getPrimaryProcedureIndicator () {
        return primaryProcedureIndicator;
    }
    public void setPrimaryProcedureIndicator (int primaryProcedureIndicator) { this.primaryProcedureIndicator = primaryProcedureIndicator; }

    @Basic
    @Column(name = "surgeon_personnel_id")
    public Integer getSurgeonPersonnelId () {
        return surgeonPersonnelId;
    }
    public void setSurgeonPersonnelId (int surgeonPersonnelId ) { this.surgeonPersonnelId = surgeonPersonnelId; }

    @Basic
    @Column(name = "dt_start")
    public Date getDTStart () {
        return dtStart;
    }
    public void setDTStart (Date dtStart) { this.dtStart = dtStart; }

    @Basic
    @Column(name = "dt_stop")
    public Date getDTStop () {
        return dtStop;
    }
    public void setDTStop (Date dtStop) {
        this.dtStop = dtStop;
    }

    @Basic
    @Column(name = "wound_class_code")
    public String getWoundClassCode () {
        return woundClassCode;
    }
    public void setWoundClassCode (String woundClassCode) {
        this.woundClassCode = woundClassCode;
    }

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }

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


