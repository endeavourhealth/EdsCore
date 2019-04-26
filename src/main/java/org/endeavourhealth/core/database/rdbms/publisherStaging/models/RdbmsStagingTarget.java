package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingTarget;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_target")
public class RdbmsStagingTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private int personId;
    private int encounterId;
    private int performerPersonnelId;
    private Date dtPerformed;
    private String freeText;
    private int recordeByPersonnelId;
    private Date dtRecorded;
    private String procedureType;
    private String procedureCode;
    private String procedureTerm;
    private int procedureSeqNbr;
    private String parentProcedureUniqueId;
    private String qualifier;
    private String location;
    private String speciality;
    private String auditJson;

    public RdbmsStagingTarget() {}

    public RdbmsStagingTarget(StagingTarget proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.uniqueId = proxy.getUniqueId();
        this.isDeleted = proxy.getIsDeleted();
        this.personId = proxy.getPersonId();
        this.encounterId = proxy.getEncounterId();
        this.performerPersonnelId = proxy.getPerformerPersonnelId();
        this.dtPerformed = proxy.getDtPerformed();
        this.freeText = proxy.getFreeText();
        this.recordeByPersonnelId = proxy.getRecordByPersonnelId();
        this.dtRecorded = proxy.getDtRecorded();
        this.procedureType = proxy.getProcedureType();
        this.procedureCode = proxy.getProcedureCode();
        this.procedureTerm = proxy.getProcedureTerm();
        this.procedureSeqNbr = proxy.getProcedureSeqNbr();
        this.parentProcedureUniqueId = proxy.getParentProcedureUniqueId();
        this.qualifier = proxy.getQualifier();
        this.location = proxy.getLocation();
        this.speciality = proxy.getSpeciality();

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
    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Basic
    @Column(name = "is_delete")
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Basic
    @Column(name = "person_id")
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) {this.personId = personId; }

    @Basic
    @Column(name = "encounter_id")
    public int getEncounterId() { return encounterId; }
    public void setEncounterId(int encounterId) {this.encounterId = encounterId; }

    @Basic
    @Column(name = "performer_personnel_id")
    public int getPerformerPersonnelId() {
        return performerPersonnelId;
    }
    public void setPerformerPersonnelId(int performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    @Basic
    @Column(name = "dt_performed")
    public Date getDtPerformed() {
        return dtPerformed;
    }
    public void setDtPerformed(Date dtPerformed) { this.dtPerformed = dtPerformed; }

    @Basic
    @Column(name = "free_text")
    public String getFreeText() {
        return freeText;
    }
    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    @Basic
    @Column(name = "recorded_by_personnel_id")
    public int getRecordByPersonnelId() {
        return recordeByPersonnelId;
    }
    public void setRecordByPersonnelId (int recordeByPersonnelId) {
        this.recordeByPersonnelId = recordeByPersonnelId;
    }

    @Basic
    @Column(name = "dt_recorded")
    public Date getDtRecorded () {
        return dtRecorded;
    }
    public void setDtRecorded (Date dtRecorded) {
        this.dtRecorded = dtRecorded;
    }

    @Basic
    @Column(name = "procedure_type")
    public String getProcedureType() {
        return procedureType;
    }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }

    @Basic
    @Column(name = "procedure_code")
    public String getProcedureCode() {
        return procedureCode;
    }
    public void setProcedureCode (String procedureCode) { this.procedureCode = procedureCode; }

    @Basic
    @Column(name = "procedure_term")
    public String getProcedureTerm () {
        return procedureTerm;
    }
    public void setProcedureTerm (String procedureTerm) { this.procedureTerm = procedureTerm; }

    @Basic
    @Column(name = "sequence_number")
    public int getProcedureSeqNbr () {
        return procedureSeqNbr;
    }
    public void setProcedureSeqNbr (int procedureSeqNbr) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    @Basic
    @Column(name = "parent_procedure_unique_id")
    public String getParentProcedureUniqueId() {
        return parentProcedureUniqueId;
    }
    public void setParentProcedureUniqueId(String parentProcedureUniqueId) {
        this.parentProcedureUniqueId = parentProcedureUniqueId;
    }

    @Basic
    @Column(name="qualifier")
    public String getQualifier() {return this.qualifier;}
    public void setQualifier(String qualifier) {this.qualifier = qualifier;}

    @Basic
    @Column(name="location")
    public String getLocation() {return this.location;}
    public void setLocation(String location) {this.location = location;}

    @Basic
    @Column(name="speciality")
    public String getSpeciality() {return this.speciality;}
    public void setSpeciality(String speciality) {this.speciality = speciality;}

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }

    @Override
    public int hashCode() {

        return Objects.hash(uniqueId,
                            isDeleted,
                            personId,
                            encounterId,
                            performerPersonnelId,
                            dtPerformed,
                            freeText,
                            recordeByPersonnelId,
                            dtRecorded,
                            procedureType,
                            procedureCode,
                            procedureTerm,
                            procedureSeqNbr,
                            parentProcedureUniqueId,
                            qualifier,
                            location,
                            speciality);
    }
}