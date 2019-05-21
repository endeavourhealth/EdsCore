package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingProcedure {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
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
    private String ward;
    private String site;
    private Integer lookupPersonId;
    private Integer lookupConsultantPersonnelId;
    private Integer lookupRecordedByPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingProcedure() {
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

    public int getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
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

    public Date getCreateDtTm() {
        return createDtTm;
    }

    public void setCreateDtTm(Date createDtTm) {
        this.createDtTm = createDtTm;
    }

    public String getProcCdType() {
        return procCdType;
    }

    public void setProcCdType(String procCdType) {
        this.procCdType = procCdType;
    }

    public String getProcCd() {
        return procCd;
    }

    public void setProcCd(String procCd) {
        this.procCd = procCd;
    }

    public String getProcTerm() {
        return procTerm;
    }

    public void setProcTerm(String procTerm) {
        this.procTerm = procTerm;
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

    public Integer getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(Integer lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    public Integer getLookupConsultantPersonnelId() {
        return lookupConsultantPersonnelId;
    }

    public void setLookupConsultantPersonnelId(Integer lookupConsultantPersonnelId) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
    }

    public Integer getLookupRecordedByPersonnelId() {
        return lookupRecordedByPersonnelId;
    }

    public void setLookupRecordedByPersonnelId(Integer lookupRecordedByPersonnelId) {
        this.lookupRecordedByPersonnelId = lookupRecordedByPersonnelId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        //only hash non-primary key fields
        return Objects.hash(
                mrn,
                nhsNumber,
                dateOfBirth,
                consultant,
                updatedBy,
                comments,
                createDtTm,
                procCdType,
                procTerm,
                ward,
                site,
                lookupPersonId,
                lookupConsultantPersonnelId,
                lookupRecordedByPersonnelId
        );
    }
}


