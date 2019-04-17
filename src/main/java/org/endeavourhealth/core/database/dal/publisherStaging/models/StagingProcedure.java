package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.UUID;

public class StagingProcedure {

    private UUID serviceId;
    private String exchangeId;
    private Date dateReceived;
    private int checkSum;
    private String mrn;
    private String nhsNumber;
    private Date dob;
    private int encounterId;
    private String consultant;
    private Date proc_dt_tm;
    private int updatedBy;
    private String comments;
    private Date create_dt_tm;
    private String procedureCodeType;
    private String procedureCode;
    private String procedureTerm;
    private int personId;
    private String ward;
    private String site;
    private int lookupPersonId;
    private int lookupConsultantPersonnelId;
    private int lookuprecordedByPersonnelId;

    private ResourceFieldMappingAudit audit = null;

    public StagingProcedure() {
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
    }

    public int getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(int lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    public int getLookupConsultantPersonnelId() {
        return lookupConsultantPersonnelId;
    }

    public void setLookupConsultantPersonnelId(int lookupConsultantPersonnelId) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
    }

    public int getLookuprecordedByPersonnelId() {
        return lookuprecordedByPersonnelId;
    }

    public void setLookuprecordedByPersonnelId(int lookuprecordedByPersonnelId) {
        this.lookuprecordedByPersonnelId = lookuprecordedByPersonnelId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
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

