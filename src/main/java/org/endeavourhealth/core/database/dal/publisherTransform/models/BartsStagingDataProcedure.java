package org.endeavourhealth.core.database.dal.publisherTransform.models;

import java.util.Date;
import java.util.UUID;

public class BartsStagingDataProcedure {
    private String exchangeId;
    private UUID serviceId;
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

    public BartsStagingDataProcedure() { }

    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public int getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
    }

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


    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }



//        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
//            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
//        }
    }

  

//    public ResourceFieldMappingAudit getAudit() {
//        return audit;
//    }
//
//    public void setAudit(ResourceFieldMappingAudit audit) {
//        this.audit = audit;
//    }

