package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingTarget;

import java.util.Date;
import java.util.Objects;

public class StagingTarget {

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
    private String specialty;

    private ResourceFieldMappingAudit audit = null;

    public StagingTarget() {}

    public StagingTarget(RdbmsStagingTarget proxy) throws Exception {

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

        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

    public String getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getUniqueId() {
        return uniqueId;
    }
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getPersonId() { return personId; }
    public void setPersonId(int personId) {this.personId = personId; }

    public int getEncounterId() { return encounterId; }
    public void setEncounterId(int encounterId) {this.encounterId = encounterId; }

    public int getPerformerPersonnelId() {
        return performerPersonnelId;
    }
    public void setPerformerPersonnelId(int performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    public Date getDtPerformed() {
        return dtPerformed;
    }
    public void setDtPerformed(Date dtPerformed) { this.dtPerformed = dtPerformed; }

    public String getFreeText() {
        return freeText;
    }
    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public int getRecordByPersonnelId() {
        return recordeByPersonnelId;
    }
    public void setRecordByPersonnelId (int recordeByPersonnelId) {
        this.recordeByPersonnelId = recordeByPersonnelId;
    }

    public Date getDtRecorded () {
        return dtRecorded;
    }
    public void setDtRecorded (Date dtRecorded) {
        this.dtRecorded = dtRecorded;
    }

    public String getProcedureType() {
        return procedureType;
    }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }

    public String getProcedureCode() {
        return procedureCode;
    }
    public void setProcedureCode (String procedureCode) { this.procedureCode = procedureCode; }

    public String getProcedureTerm () {
        return procedureTerm;
    }
    public void setProcedureTerm (String procedureTerm) { this.procedureTerm = procedureTerm; }

    public int getProcedureSeqNbr () {
        return procedureSeqNbr;
    }
    public void setProcedureSeqNbr (int procedureSeqNbr) {
        this.procedureSeqNbr = procedureSeqNbr;
    }

    public String getParentProcedureUniqueId() {
        return parentProcedureUniqueId;
    }
    public void setParentProcedureUniqueId(String parentProcedureUniqueId) {
        this.parentProcedureUniqueId = parentProcedureUniqueId;
    }

    public String getQualifier() {return this.qualifier;}
    public void setQualifier(String qualifier) {this.qualifier = qualifier;}

    public String getLocation() {return this.location;}
    public void setLocation(String location) {this.location = location;}

    public String getSpecialty() {return this.specialty;}
    public void setSpecialty(String specialty) {this.specialty = specialty;}

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }
    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

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
                specialty);
    }

    @Override
    public String toString() {
        return "StagingTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", dtPerformed=" + dtPerformed +
                ", freeText='" + freeText + '\'' +
                ", recordeByPersonnelId='" + recordeByPersonnelId + '\'' +
                ", dtRecorded=" + dtRecorded +
                ", procedureType='" + procedureType + '\'' +
                ", procedureCode=" + procedureCode +
                ", procedureTerm='" + procedureTerm + '\'' +
                ", procedureSeqNbr=" + procedureSeqNbr +
                ", parentProcedureUniqueId='" + parentProcedureUniqueId + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", location=" + location +
                ", speciality=" + specialty +
                ", audit=" + audit +
                '}';
    }
}