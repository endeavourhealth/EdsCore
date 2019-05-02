package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingSURCC {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private int surgicalCaseId;
    private Date dtExtract;
    private boolean activeInd;
    private Integer personId;
    private Integer encounterId;
    private Date dtCancelled;
    private String institutionCode;
    private String departmentCode;
    private String surgicalAreaCode;
    private String theatreNumberCode;
    private String specialtyCode;
    private ResourceFieldMappingAudit audit = null;

    public StagingSURCC() {}

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

    public int getSurgicalCaseId() {
        return surgicalCaseId;
    }

    public void setSurgicalCaseId(int surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    public Date getDtExtract() {
        return dtExtract;
    }

    public void setDtExtract(Date dtExtract) {
        this.dtExtract = dtExtract;
    }

    public boolean isActiveInd() {
        return activeInd;
    }

    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    public Date getDtCancelled() {
        return dtCancelled;
    }

    public void setDtCancelled(Date dtCancelled) {
        this.dtCancelled = dtCancelled;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getSurgicalAreaCode() {
        return surgicalAreaCode;
    }

    public void setSurgicalAreaCode(String surgicalAreaCode) {
        this.surgicalAreaCode = surgicalAreaCode;
    }

    public String getTheatreNumberCode() {
        return theatreNumberCode;
    }

    public void setTheatreNumberCode(String theatreNumberCode) {
        this.theatreNumberCode = theatreNumberCode;
    }

    public String getSpecialtyCode() {
        return specialtyCode;
    }

    public void setSpecialtyCode(String specialtyCode) {
        this.specialtyCode = specialtyCode;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public int hashCode() {

        return Objects.hash(surgicalCaseId,
                            activeInd,
                            personId,
                            encounterId,
                            dtCancelled,
                            institutionCode,
                            departmentCode,
                            surgicalAreaCode,
                            theatreNumberCode,
                            specialtyCode);
    }
}