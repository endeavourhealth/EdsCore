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
    private String personId;
    private String ward;
    private String site;
    private String lookupPersonId;
    private int lookupConsultantPersonnelId;
    private int lookuprecordedByPersonnelId;
    private String auditJson;

    public RdbmsStagingProcedure(StagingProcedure in) throws Exception {
        this.exchangeId = in.getExchangeId();
        this.dateReceived = in.getDateReceived();
        this.checkSum = in.getCheckSum();
        this.mrn = in.getMrn();
        this.nhsNumber = in.getNhsNumber();
        this.dob = in.getDob();
        this.encounterId = in.getEncounterId();
        this.personId = in.getPersonId();
        this.ward = in.getWard();
        this.site = in.getSite();
        this.consultant = in.getConsultant();
        this.proc_dt_tm = in.getProc_dt_tm();
        this.create_dt_tm = in.getCreate_dt_tm();
        this.updatedBy = in.getUpdatedBy();
        this.comments = in.getComments();
        this.procedureCode = in.getProcedureCode();
        this.procedureCodeType = in.getProcedureCodeType();
        this.procedureTerm = in.getProcedureTerm();
        this.lookupPersonId = in.getLookupPersonId();
        this.lookupConsultantPersonnelId = in.getLookupConsultantPersonnelId();
        this.lookuprecordedByPersonnelId = in.getLookuprecordedByPersonnelId();

        if (in.getAudit()!= null) {
            this.auditJson = in.getAudit().writeToJson();
        }
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
    public Date getProc_dt_tm() {
        return proc_dt_tm;
    }

    public void setProc_dt_tm(Date proc_dt_tm) {
        this.proc_dt_tm = proc_dt_tm;
    }

    @Column(name="create_dt_tm")
    public Date getCreate_dt_tm() {
        return create_dt_tm;
    }

    public void setCreate_dt_tm(Date create_dt_tm) {
        this.create_dt_tm = create_dt_tm;
    }

    @Column(name="updated_by")
    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }


    @Column(name="proc_cd")
    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    @Column(name="proc_cd_type")
    public String getProcedureCodeType() {
        return procedureCodeType;
    }

    public void setProcedureCodeType(String procedureCodeType) {
        this.procedureCodeType = procedureCodeType;
    }

    @Column(name="dt_received")
    public Date getDTReceived() {
        return dateReceived;
    }

    public void setDTReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
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
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Column(name="freetext_comment")
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name="proc_term")
    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
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
                            dob,
                            encounterId,
                            personId,
                            ward,
                            site,
                            consultant,
                            proc_dt_tm,
                            create_dt_tm,
                            updatedBy,
                            comments,
                            procedureCode,
                            procedureCodeType,
                            procedureTerm,
                            lookupPersonId,
                            lookupConsultantPersonnelId,
                            lookuprecordedByPersonnelId
                );
    }
}
