package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingEmergencyCdsTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer encounterId;
    private Integer episodeId;
    private Integer performerPersonnelId;

    private String departmentType;
    private String ambulanceNo;
    private String organisationCode;
    private String attendanceId;
    private String arrivalMode;
    private String attendanceCategory;
    private String attendanceSource;
    private Date dtArrival;
    private Date dtInitialAssessment;
    private String chiefComplaint;
    private Date dtSeenForTreatment;
    private Date dtDecidedToAdmit;
    private String treatmentFunctionCode;
    private String dischargeStatus;
    private String dischargeDestination;
    private Date dtConclusion;
    private Date dtDeparture;
    private String diagnosis;
    private String investigations;
    private String treatments;
    private String referredToServices;
    private String safeguardingConcerns;

    private Boolean isConfidential;
    private ResourceFieldMappingAudit audit = null;

    public StagingEmergencyCdsTarget() {}

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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public Integer getPerformerPersonnelId() {
        return performerPersonnelId;
    }

    public void setPerformerPersonnelId(Integer performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    public String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public String getAmbulanceNo() {
        return ambulanceNo;
    }

    public void setAmbulanceNo(String ambulanceNo) {
        this.ambulanceNo = ambulanceNo;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getArrivalMode() {
        return arrivalMode;
    }

    public void setArrivalMode(String arrivalMode) {
        this.arrivalMode = arrivalMode;
    }

    public String getAttendanceCategory() {
        return attendanceCategory;
    }

    public void setAttendanceCategory(String attendanceCategory) {
        this.attendanceCategory = attendanceCategory;
    }

    public String getAttendanceSource() {
        return attendanceSource;
    }

    public void setAttendanceSource(String attendanceSource) {
        this.attendanceSource = attendanceSource;
    }

    public Date getDtArrival() {
        return dtArrival;
    }

    public void setDtArrival(Date dtArrival) {
        this.dtArrival = dtArrival;
    }

    public Date getDtInitialAssessment() {
        return dtInitialAssessment;
    }

    public void setDtInitialAssessment(Date dtInitialAssessment) {
        this.dtInitialAssessment = dtInitialAssessment;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public Date getDtSeenForTreatment() {
        return dtSeenForTreatment;
    }

    public void setDtSeenForTreatment(Date dtSeenForTreatment) {
        this.dtSeenForTreatment = dtSeenForTreatment;
    }

    public Date getDtDecidedToAdmit() {
        return dtDecidedToAdmit;
    }

    public void setDtDecidedToAdmit(Date dtDecidedToAdmit) {
        this.dtDecidedToAdmit = dtDecidedToAdmit;
    }

    public String getTreatmentFunctionCode() {
        return treatmentFunctionCode;
    }

    public void setTreatmentFunctionCode(String treatmentFunctionCode) {
        this.treatmentFunctionCode = treatmentFunctionCode;
    }

    public String getDischargeStatus() {
        return dischargeStatus;
    }

    public void setDischargeStatus(String dischargeStatus) {
        this.dischargeStatus = dischargeStatus;
    }

    public String getDischargeDestination() {
        return dischargeDestination;
    }

    public void setDischargeDestination(String dischargeDestination) {
        this.dischargeDestination = dischargeDestination;
    }

    public Date getDtConclusion() {
        return dtConclusion;
    }

    public void setDtConclusion(Date dtConclusion) {
        this.dtConclusion = dtConclusion;
    }

    public Date getDtDeparture() {
        return dtDeparture;
    }

    public void setDtDeparture(Date dtDeparture) {
        this.dtDeparture = dtDeparture;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getInvestigations() {
        return investigations;
    }

    public void setInvestigations(String investigations) {
        this.investigations = investigations;
    }

    public String getTreatments() {
        return treatments;
    }

    public void setTreatments(String treatments) {
        this.treatments = treatments;
    }

    public String getReferredToServices() {
        return referredToServices;
    }

    public void setReferredToServices(String referredToServices) {
        this.referredToServices = referredToServices;
    }

    public String getSafeguardingConcerns() {
        return safeguardingConcerns;
    }

    public void setSafeguardingConcerns(String safeguardingConcerns) {
        this.safeguardingConcerns = safeguardingConcerns;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    public Boolean isConfidential() {
        return isConfidential;
    }

    public void setConfidential(Boolean confidential) {
        isConfidential = confidential;
    }

    @Override
    public String toString() {
        return "StagingEmergencyCdsTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", encounterId='" + encounterId + '\'' +
                ", episodeId='" + episodeId + '\'' +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", departmentType='" + departmentType + '\'' +
                ", ambulanceNo='" + ambulanceNo + '\'' +
                ", organisationCode='" + organisationCode + '\'' +
                ", attendanceId='" + attendanceId + '\'' +
                ", arrivalMode='" + arrivalMode + '\'' +
                ", attendanceCategory='" + attendanceCategory + '\'' +
                ", attendanceSource='" + attendanceSource + '\'' +
                ", dtArrival='" + dtArrival + '\'' +
                ", dtInitialAssessment='" + dtInitialAssessment + '\'' +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", dtSeenForTreatment='" + dtSeenForTreatment + '\'' +
                ", dtDecidedToAdmit='" + dtDecidedToAdmit + '\'' +
                ", treatmentFunctionCode='" + treatmentFunctionCode + '\'' +
                ", dischargeStatus='" + dischargeStatus + '\'' +
                ", dischargeDestination='" + dischargeDestination + '\'' +
                ", dtConclusion='" + dtConclusion + '\'' +
                ", dtDeparture='" + dtDeparture + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", investigations='" + investigations + '\'' +
                ", treatments='" + treatments + '\'' +
                ", referredToServices='" + referredToServices + '\'' +
                ", safeguardingConcerns='" + safeguardingConcerns + '\'' +
                ", audit=" + audit +
                ", isConfidential=" + isConfidential +
                '}';
    }
}