package org.endeavourhealth.core.database.dal.publisherStaging.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingSURCC;

import java.util.Date;
import java.util.Objects;

public class StagingSURCC {
    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date cdsActivityDate;
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
    private ResourceFieldMappingAudit audit = null;

    public StagingSURCC() {}

    public StagingSURCC(RdbmsStagingSURCC proxy) throws Exception {
        this.exchangeId = proxy.getExchangeId();
        this.dtReceived = proxy.getDtReceived();
        this.recordChecksum = proxy.getRecordChecksum();
        this.cdsActivityDate = proxy.getCdsActivityDate();
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

    public Date getDTReceived() {
        return dtReceived;
    }
    public void setDTReceived(Date dtReceived) {
        this.dtReceived = dtReceived;
    }

    public int getRecordChecksum() {return recordChecksum;}
    public void setRecordChecksum(int recordChecksum) {
        this.recordChecksum = recordChecksum;
    }

    public Date getDtReceived() {return dtReceived;}
    public void setDtReceived(Date dtReceived) {this.dtReceived = dtReceived;}

    public Date getCdsActivityDate() {return cdsActivityDate;}
    public void setCdsActivityDate(Date cdsActivityDate) {this.cdsActivityDate = cdsActivityDate;}

    public Date getDtExtract() {return dtExtract;}
    public void setDtExtract(Date dtExtract) {this.dtExtract = dtExtract;}

    public boolean isActiveInd() {return activeInd;}

    public Date getDtCancelled() {return dtCancelled;}
    public void setDtCancelled(Date dtCancelled) {this.dtCancelled = dtCancelled;}

    public int getSurgicalCaseId() {
        return surgicalCaseId;
    }
    public void setSurgicalCaseId(int surgicalCaseId) {
        this.surgicalCaseId = surgicalCaseId;
    }

    public Date getDTExtract() {
        return dtExtract;
    }
    public void setDTExtract(Date dtExtract) { this.dtExtract = dtExtract; }

    public boolean getActiveInd() {
        return activeInd;
    }
    public void setActiveInd(boolean activeInd) {
        this.activeInd = activeInd;
    }

    public int getPersonId  () {
        return personId ;
    }
    public void setPersonId (int personId ) {
        this.personId = personId;
    }

    public int getEncounterId () {
        return encounterId ;
    }
    public void setEncounterId (int encounterId ) {
        this.encounterId = encounterId;
    }

    public Date getDTCancelled () {
        return dtCancelled;
    }
    public void setDTCancelled (Date dtCancelled ) {
        this.dtCancelled = dtCancelled;
    }

    public String getInstitutionCode () {
        return institutionCode;
    }
    public void setInstitutionCode (String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getDepartmentCode () {
        return departmentCode;
    }
    public void setDepartmentCode (String departmentCode ) { this.departmentCode = departmentCode; }

    public String getSurgicalAreaCode () {
        return surgicalAreaCode;
    }
    public void setSurgicalAreaCode (String surgicalAreaCode) { this.surgicalAreaCode = surgicalAreaCode; }

    public String getTheatreNumberCode () {
        return theatreNumberCode;
    }
    public void setTheatreNumberCode (String theatreNumberCode) {
        this.theatreNumberCode = theatreNumberCode;
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