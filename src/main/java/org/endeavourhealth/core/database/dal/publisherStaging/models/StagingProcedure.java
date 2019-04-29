package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class StagingProcedure {

    private UUID serviceId;
    private String exchangeId;
    private Date dtReceived;
    private int checkSum;
    private String mrn;
    private String nhsNumber;
    private Date dateOfBirth;
    private int encounterId;
    private String consultant;
    private Date procDtTm;
    private String updatedBy;
    private String comments;
    private Date createDtTm;
    private String procCdType;
    private String procCd;
    private String procTerm;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getProcTerm() {
        return procTerm;
    }

    public void setProcTerm(String procTerm) {
        this.procTerm = procTerm;
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

    public String getFreeTextComment() {
        return comments;
    }

    public void setFreeTextComment(String comments) {
        this.comments = comments;
    }

    public String getProcCd() { return procCd; }

    public void setProcCd(String procCd) {
        this.procCd = procCd;
    }

    public String getProcCdType() {
        return procCdType;
    }

    public void setProcCdType(String procCdType) {
        this.procCdType = procCdType;
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

        return Objects.hash(mrn,
                nhsNumber,
                dateOfBirth,
                encounterId,
                personId,
                ward,
                site,
                consultant,
                procDtTm,
                createDtTm,
                updatedBy,
                comments,
                procCd,
                procCdType,
                procTerm,
                lookupPersonId,
                lookupConsultantPersonnelId,
                lookuprecordedByPersonnelId
        );
    }

//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder()
//                .append(serviceId)
//                .append(exchangeId)
//                .append(dtReceived)
//                .append(checkSum)
//                .append(mrn)
//                .append(nhsNumber)
//                .append(dateOfBirth)
//                .append(encounterId)
//                .append(consultant)
//                .append(procDtTm)
//                .append(updatedBy)
//                .append(comments)
//                .append(createDtTm)
//                .append(procCdType)
//                .append(procCd)
//                .append(procTerm)
//                .append(personId)
//                .append(ward)
//                .append(site)
//                .append(lookupPersonId)
//                .append(lookupConsultantPersonnelId)
//                .append(lookuprecordedByPersonnelId)
//                .toHashCode();
//    }

}


