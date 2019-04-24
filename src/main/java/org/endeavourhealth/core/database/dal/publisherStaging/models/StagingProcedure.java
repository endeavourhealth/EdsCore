package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.UUID;

public class StagingProcedure {

    private UUID serviceId;
    private String exchangeId;
    private Date dtReceived;
    private int checkSum;
    private String mrn;
    private String nhsNumber;
    private Date dob;
    private int encounterId;
    private String consultant;
    private Date procDtTm;
    private String updatedBy;
    private String comments;
    private Date createDtTm;
    private String procedureCodeType;
    private String procedureCode;
    private String procedureTerm;
    private String personId;
    private String ward;
    private String site;
    private String lookupPersonId;
    private int lookupConsultantPersonnelId;
    private int lookuprecordedByPersonnelId;

    private ResourceFieldMappingAudit audit = null;

    public StagingProcedure() {
    }

    public Date getDtReceived() {
        return dtReceived;
    }

    public void setDateReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
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

    public String getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(String lookupPersonId) {
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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
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

    public Date getProcDtTm() {
        return procDtTm;
    }

    public void setProcDtTm(Date procDtTm) {
        this.procDtTm = procDtTm;
    }

    public Date getCreateDtTm() {
        return createDtTm;
    }

    public void setCreateDtTm(Date createDtTm) {
        this.createDtTm = createDtTm;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
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

    public void setCheckSum() {
        this.checkSum = hashCode();
    }


    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }



    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(serviceId)
                .append(exchangeId)
                .append(dtReceived)
                .append(checkSum)
                .append(mrn)
                .append(nhsNumber)
                .append(dob)
                .append(encounterId)
                .append(consultant)
                .append(procDtTm)
                .append(updatedBy)
                .append(comments)
                .append(createDtTm)
                .append(procedureCodeType)
                .append(procedureCode)
                .append(procedureTerm)
                .append(personId)
                .append(ward)
                .append(site)
                .append(lookupPersonId)
                .append(lookupConsultantPersonnelId)
                .append(lookuprecordedByPersonnelId)
                .toHashCode();
    }


//        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
//            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
//        }
}


