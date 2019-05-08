package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_target")
public class RdbmsStagingTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer performerPersonnelId;
    private Date dtPerformed;
    private Date dtEnded;
    private String freeText;
    private Integer recordByPersonnelId;
    private Date dtRecorded;
    private String procedureType;
    private String procedureCode;
    private String procedureTerm;
    private Integer procedureSeqNbr;
    private String parentProcedureUniqueId;
    private String qualifier;
    private String location;
    private String specialty;
    private String auditJson;

    public RdbmsStagingTarget() {
    }

    /*public RdbmsStagingTarget(StagingTarget proxy) throws Exception {

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
        this.specialty = proxy.getSpecialty();

        if (proxy.getAudit()!= null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }*/

    @Id
    @Column(name = "exchange_id")
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Column(name = "unique_id")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "is_delete")
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Column(name = "person_id")
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    @Column(name = "encounter_id")
    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    @Column(name = "performer_personnel_id")
    public Integer getPerformerPersonnelId() {
        return performerPersonnelId;
    }

    public void setPerformerPersonnelId(Integer performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    @Column(name = "dt_performed")
    public Date getDtPerformed() {
        return dtPerformed;
    }

    public void setDtPerformed(Date dtPerformed) {
        this.dtPerformed = dtPerformed;
    }

    public Date getDtEnded() {return dtEnded;}

    public void setDtEnded(Date dtEnded) {this.dtEnded = dtEnded;    }

    @Column(name = "free_text")
    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    @Column(name = "recorded_by_personnel_id")
    public Integer getRecordByPersonnelId() {
        return recordByPersonnelId;
    }

    public void setRecordByPersonnelId(Integer recordByPersonnelId) {
        this.recordByPersonnelId = recordByPersonnelId;
    }

    @Column(name = "dt_recorded")
    public Date getDtRecorded() {
        return dtRecorded;
    }

    public void setDtRecorded(Date dtRecorded) {
        this.dtRecorded = dtRecorded;
    }

    @Column(name = "procedure_type")
    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    @Column(name = "procedure_code")
    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    @Column(name = "procedure_term")
    public String getProcedureTerm() {
        return procedureTerm;
    }

    public void setProcedureTerm(String procedureTerm) {
        this.procedureTerm = procedureTerm;
    }

    @Column(name = "sequence_number")
    public Integer getProcedureSeqNbr() {
        return procedureSeqNbr;
    }

    public void setProcedureSeqNbr(Integer procedureSeqNbr) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    @Column(name = "parent_procedure_unique_id")
    public String getParentProcedureUniqueId() {
        return parentProcedureUniqueId;
    }

    public void setParentProcedureUniqueId(String parentProcedureUniqueId) {
        this.parentProcedureUniqueId = parentProcedureUniqueId;
    }

    @Column(name = "qualifier")
    public String getQualifier() {
        return this.qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Column(name = "location")
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "specialty")
    public String getSpecialty() {
        return this.specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    @Override
    public int hashCode() {

        return Objects.hash(uniqueId,
                isDeleted,
                personId,
                encounterId,
                performerPersonnelId,
                dtPerformed,
                dtEnded,
                freeText,
                recordByPersonnelId,
                dtRecorded,
                procedureType,
                procedureCode,
                procedureTerm,
                procedureSeqNbr,
                parentProcedureUniqueId,
                qualifier,
                location,
                specialty);
    }
}