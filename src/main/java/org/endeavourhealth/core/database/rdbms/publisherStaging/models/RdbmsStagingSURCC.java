package org.endeavourhealth.core.database.rdbms.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCC;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "procedure_SURCC")
public class RdbmsStagingSURCC {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int surgicalCaseId;
    private Date dtExtract;
    private boolean activeInd;
    private int personId;
    private int encounterId;
    private Date dtCancelled;
    private String institutionCode;
    private String departmentCode;
    private String surgicalAreaCode;
    private String theatreNumberCode;
    private String auditJson;

    public RdbmsStagingSURCC() {}

    public RdbmsStagingSURCC(StagingSURCC proxy) throws Exception {

        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDTReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.surgicalCaseId = proxy.getSurgicalCaseId();
        this.dtExtract = proxy.getDTExtract();
        this.activeInd = proxy.getActiveInd();
        this.personId = proxy.getPersonId();
        this.encounterId = proxy.getEncounterId();
        this.dtCancelled = proxy.getDTCancelled();
        this.institutionCode = proxy.getInstitutionCode();
        this.departmentCode = proxy.getDepartmentCode();
        this.surgicalAreaCode = proxy.getSurgicalAreaCode();
        this.theatreNumberCode = proxy.getTheatreNumberCode();

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
    @Column(name = "dt_received")
    public Date getDtReceived() {
        return dtReceived;
    }
    public void setDtReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    @Basic
    @Column(name = "record_checksum")
    public int getRecordChecksum() {
        return recordChecksum;
    }
    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    @Basic
    @Column(name = "surgical_case_id")
    public int getSurgicalCaseId() {
        return surgicalCaseId;
    }
    public void setSurgicalCaseId(int surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    @Basic
    @Column(name = "dt_extract")
    public Date getDTExtract() {
        return dtExtract;
    }
    public void setDTExtract(Date dtExtract) { this.dtExtract = dtExtract; }

    @Basic
    @Column(name = "active_ind")
    public boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    @Basic
    @Column(name = "person_id")
    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    @Basic
    @Column(name = "encounter_id ")
    public int getEncounterId () {
        return encounterId ;
    }
    public void setEncounterId (int encounterId ) {
        this.encounterId = encounterId;
    }

    @Basic
    @Column(name = "dt_cancelled")
    public Date getDTCancelled () {
        return dtCancelled;
    }
    public void setDTCancelled (Date dtCancelled ) {
        this.dtCancelled = dtCancelled;
    }

    @Basic
    @Column(name = "institution_code")
    public String getInstitutionCode () {
        return institutionCode;
    }
    public void setInstitutionCode (String institutionCode) {
        this.institutionCode = institutionCode;
    }

    @Basic
    @Column(name = "department_code")
    public String getDepartmentCode () {
        return departmentCode;
    }
    public void setDepartmentCode (String departmentCode ) { this.departmentCode = departmentCode; }

    @Basic
    @Column(name = "surgical_area_code")
    public String getSurgicalAreaCode () {
        return surgicalAreaCode;
    }
    public void setSurgicalAreaCode (String surgicalAreaCode) { this.surgicalAreaCode = surgicalAreaCode;
    }

    @Basic
    @Column(name = "theatre_number_code")
    public String getTheatreNumberCode () {
        return theatreNumberCode;
    }
    public void setTheatreNumberCode (String theatreNumberCode) {
        this.theatreNumberCode = theatreNumberCode;
    }

    @Basic
    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() { return auditJson; }
    public void setAuditJson(String auditJson) {this.auditJson = auditJson; }

    @Override
    public int hashCode() {

        return Objects.hash(surgicalCaseId,
                            dtExtract,
                            activeInd,
                            personId,
                            encounterId,
                            dtCancelled,
                            institutionCode,
                            departmentCode,
                            surgicalAreaCode,
                            theatreNumberCode);
    }
}
