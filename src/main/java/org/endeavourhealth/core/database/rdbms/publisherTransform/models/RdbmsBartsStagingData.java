package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.BartsStagingDataProcedure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "staging_procedure")
public class RdbmsBartsStagingData implements Serializable {

    private String exchangeId;
    private int encounterId;
    private int personId;
    private String ward;
    private String site;
    private String consultant;
    private Date proc_dt_tm;
    private Date create_dt_tm;
    private int updatedBy;
    private String notes;
    private String procedureCode;
    private String procedureCodeType;
    private int comparisonCode;
    private String auditJson;


    public RdbmsBartsStagingData(BartsStagingDataProcedure in) {
        this.exchangeId = in.getExchangeId();
        this.encounterId = in.getEncounterId();
        this.personId = in.getPersonId();
        this.ward = in.getWard();
        this.site = in.getSite();
        this.consultant = in.getConsultant();
        this.proc_dt_tm = in.getProc_dt_tm();
        this.create_dt_tm = in.getCreate_dt_tm();
        this.updatedBy = in.getUpdatedBy();
        this.notes = in.getNotes();
        this.procedureCode = in.getProcedureCode();
        this.procedureCodeType = in.getProcedureCodeType();
        this.comparisonCode = in.getComparisonCode();
        this.auditJson = in.getAuditJson();
    }

    @Id
    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {return exchangeId;}

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }
    @Column(name = "encounter_id", nullable = false)
    public int getEncounterId() {return encounterId;    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
    }

   @Column(name="")
    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public Date getProc_dt_tm() {
        return proc_dt_tm;
    }

    public void setProc_dt_tm(Date proc_dt_tm) {
        this.proc_dt_tm = proc_dt_tm;
    }

    public Date getCreate_dt_tm() {
        return create_dt_tm;
    }

    public void setCreate_dt_tm(Date create_dt_tm) {
        this.create_dt_tm = create_dt_tm;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureCodeType() {
        return procedureCodeType;
    }

    public void setProcedureCodeType(String procedureCodeType) {
        this.procedureCodeType = procedureCodeType;
    }

    public int getComparisonCode() {
        return comparisonCode;
    }

    public void setComparisonCode(int comparisonCode) {
        this.comparisonCode = comparisonCode;
    }

    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

}
