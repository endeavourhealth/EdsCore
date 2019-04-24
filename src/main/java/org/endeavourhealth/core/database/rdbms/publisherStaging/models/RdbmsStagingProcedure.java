package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_procedure")
public class RdbmsStagingProcedure implements Serializable {
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
    private String auditJson;

    public RdbmsStagingProcedure(StagingProcedure in) throws Exception {
        this.exchangeId = in.getExchangeId();
        this.dtReceived = in.getDtReceived();
        this.checkSum = in.getCheckSum();
        this.mrn = in.getMrn();
        this.nhsNumber = in.getNhsNumber();
        this.dateOfBirth = in.getDateOfBirth();
        this.encounterId = in.getEncounterId();
        this.personId = in.getPersonId();
        this.ward = in.getWard();
        this.site = in.getSite();
        this.consultant = in.getConsultant();
        this.procDtTm = in.getProcDtTm();
        this.createDtTm = in.getCreateDtTm();
        this.updatedBy = in.getUpdatedBy();
        this.comments = in.getFreeTextComment();
        this.procCd = in.getProcCd();
        this.procCdType = in.getProcCdType();
        this.procTerm = in.getProcTerm();
        this.lookupPersonId = in.getLookupPersonId();
        this.lookupConsultantPersonnelId = in.getLookupConsultantPersonnelId();
        this.lookuprecordedByPersonnelId = in.getLookuprecordedByPersonnelId();

        if (in.getAudit()!= null) {
            this.auditJson = in.getAudit().writeToJson();
        }
    }

    public RdbmsStagingProcedure() {
    }

    @Id
    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Column(name = "encounter_id", nullable = false)
    public int getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(int encounterId) {
        this.encounterId = encounterId;
    }

    @Column(name = "person_id")
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Column(name = "ward")
    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Column(name="site")
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Column(name="consultant")
    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    @Column(name="proc_dt_tm")
    public Date getProcDtTm() {
        return procDtTm;
    }

    public void setProcDtTm(Date procDtTm) {
        this.procDtTm = procDtTm;
    }

    @Column(name="create_dt_tm")
    public Date getCreateDtTm() {
        return createDtTm;
    }

    public void setCreateDtTm(Date createDtTm) {
        this.createDtTm = createDtTm;
    }

    @Column(name="updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }


    @Column(name="proc_cd")
    public String getProcCd() {
        return procCd;
    }

    public void setProcCd(String procCd) {
        this.procCd = procCd;
    }

    @Column(name="proc_cd_type")
    public String getProcCdType() {
        return procCdType;
    }

    public void setProcCdType(String procCdType) {
        this.procCdType = procCdType;
    }

    @Column(name="dt_received")
    public Date getDtReceived() {
        return dtReceived;
    }

    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    @Column(name="record_checksum")
    public int getRecordChecksum() {
        return checkSum;
    }

    public void setRecordChecksum(int checkSum) {
        this.checkSum = checkSum;
    }

    @Column(name="mrn")
    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    @Column(name="nhs_number")
    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }

    @Column(name="date_of_birth")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name="freetext_comment")
    public String getFreeTextComment() {
        return comments;
    }

    public void setFreeTextComment(String comments) {
        this.comments = comments;
    }

    @Column(name="proc_term")
    public String getProcTerm() {
        return procTerm;
    }

    public void setProcTerm(String procTerm) {
        this.procTerm = procTerm;
    }

    @Column(name="lookup_person_id")
    public String getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(String lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    @Column(name="lookup_consultant_personnel_id")
    public int getLookupConsultantPersonnelId() {
        return lookupConsultantPersonnelId;
    }

    public void setLookupConsultantPersonnelId(int lookupConsultantPersonnelId) {
        this.lookupConsultantPersonnelId = lookupConsultantPersonnelId;
    }

    @Column(name="lookup_recorded_by_personnel_id")
    public int getLookuprecordedByPersonnelId() {
        return lookuprecordedByPersonnelId;
    }

    public void setLookuprecordedByPersonnelId(int lookuprecordedByPersonnelId) {
        this.lookuprecordedByPersonnelId = lookuprecordedByPersonnelId;
    }

    @Column(name="audit_json", nullable = true)
    public String getAuditJson() {
        return auditJson;
    }
    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
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
}
