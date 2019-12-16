package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

//Note: this schema is based on the EmergencyCareDataSet data not the older a&e data

public class StagingEmergencyCds implements Cloneable {

    private String exchangeId;
    private Date dtReceived;
    private int recordChecksum;
    private Date cdsActivityDate;
    private String cdsUniqueIdentifier;
    private int cdsUpdateType;
    private String mrn;
    private String nhsNumber;
    private Boolean withheld;
    private Date dateOfBirth;
    // private String consultantCode;
    private String patientPathwayIdentifier;

    private String departmentType;
    private String ambulanceIncidentNumber;
    private String ambulanceTrustOrganisationCode;
    private String attendanceIdentifier;
    private String arrivalMode;
    private String attendanceCategory;
    private String attendanceSource;
    private Date arrivalDate;
    private Date initialAssessmentDate;
    private String chiefComplaint;
    private Date seenForTreatmentDate;
    private Date decidedToAdmitDate;
    private String treatmentFunctionCode;
    private String dischargeStatus;
    private String dischargeDestination;
    private Date conclusionDate;
    private Date departureDate;
    private String mhClassifications;
    private String diagnosis;
    private String investigations;
    private String treatments;
    private String referredToServices;
    private String safeguardingConcerns;

    private Integer lookupPersonId;
    // private Integer lookupConsultantPersonnelId;
    private ResourceFieldMappingAudit audit = null;

    public StagingEmergencyCds() {
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

    public Date getCdsActivityDate() {
        return cdsActivityDate;
    }

    public void setCdsActivityDate(Date cdsActivityDate) {
        this.cdsActivityDate = cdsActivityDate;
    }

    public String getCdsUniqueIdentifier() {
        return cdsUniqueIdentifier;
    }

    public void setCdsUniqueIdentifier(String cdsUniqueIdentifier) {
        this.cdsUniqueIdentifier = cdsUniqueIdentifier;
    }

    public int getCdsUpdateType() {
        return cdsUpdateType;
    }

    public void setCdsUpdateType(int cdsUpdateType) {
        this.cdsUpdateType = cdsUpdateType;
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

    public Boolean getWithheld() {
        return withheld;
    }

    public void setWithheld(Boolean withheld) {
        this.withheld = withheld;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPatientPathwayIdentifier() {
        return patientPathwayIdentifier;
    }

    public void setPatientPathwayIdentifier(String patientPathwayIdentifier) {
        this.patientPathwayIdentifier = patientPathwayIdentifier;
    }

    public String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public String getAmbulanceIncidentNumber() {
        return ambulanceIncidentNumber;
    }

    public void setAmbulanceIncidentNumber(String ambulanceIncidentNumber) {
        this.ambulanceIncidentNumber = ambulanceIncidentNumber;
    }

    public String getAmbulanceTrustOrganisationCode() {
        return ambulanceTrustOrganisationCode;
    }

    public void setAmbulanceTrustOrganisationCode(String ambulanceTrustOrganisationCode) {
        this.ambulanceTrustOrganisationCode = ambulanceTrustOrganisationCode;
    }

    public String getAttendanceIdentifier() {
        return attendanceIdentifier;
    }

    public void setAttendanceIdentifier(String attendanceIdentifier) {
        this.attendanceIdentifier = attendanceIdentifier;
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

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getInitialAssessmentDate() {
        return initialAssessmentDate;
    }

    public void setInitialAssessmentDate(Date initialAssessmentDate) {
        this.initialAssessmentDate = initialAssessmentDate;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public Date getSeenForTreatmentDate() {
        return seenForTreatmentDate;
    }

    public void setSeenForTreatmentDate(Date seenForTreatmentDate) {
        this.seenForTreatmentDate = seenForTreatmentDate;
    }

    public Date getDecidedToAdmitDate() {
        return decidedToAdmitDate;
    }

    public void setDecidedToAdmitDate(Date decidedToAdmitDate) {
        this.decidedToAdmitDate = decidedToAdmitDate;
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

    public Date getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(Date conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getMhClassifications() {
        return mhClassifications;
    }

    public void setMhClassifications(String mhClassifications) {
        this.mhClassifications = mhClassifications;
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

    public Integer getLookupPersonId() {
        return lookupPersonId;
    }

    public void setLookupPersonId(Integer lookupPersonId) {
        this.lookupPersonId = lookupPersonId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }


    public StagingEmergencyCds clone() throws CloneNotSupportedException {
        return (StagingEmergencyCds) super.clone();
    }

    @Override
    public int hashCode() {

        //only calculate the hash from the non-primary key fields
        return Objects.hash(
                cdsActivityDate,
                cdsUpdateType,
                mrn,
                nhsNumber,
                withheld,
                dateOfBirth,
                patientPathwayIdentifier,
                departmentType,
                ambulanceIncidentNumber,
                ambulanceTrustOrganisationCode,
                attendanceIdentifier,
                arrivalMode,
                attendanceCategory,
                attendanceSource,
                arrivalDate,
                initialAssessmentDate,
                chiefComplaint,
                seenForTreatmentDate,
                decidedToAdmitDate,
                treatmentFunctionCode,
                dischargeStatus,
                dischargeDestination,
                conclusionDate,
                departureDate,
                mhClassifications,
                diagnosis,
                investigations,
                treatments,
                referredToServices,
                safeguardingConcerns,
                lookupPersonId);
    }

    @Override
    public String toString() {
        return "StagingEmergencyCds{" +
                "exchangeId='" + exchangeId + '\'' +
                ", dtReceived=" + dtReceived +
                ", recordChecksum=" + recordChecksum +
                ", cdsActivityDate=" + cdsActivityDate +
                ", cdsUniqueIdentifier='" + cdsUniqueIdentifier + '\'' +
                ", cdsUpdateType=" + cdsUpdateType +
                ", mrn='" + mrn + '\'' +
                ", nhsNumber='" + nhsNumber + '\'' +
                ", withheld='" + withheld + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", patientPathwayIdentifier='" + patientPathwayIdentifier + '\'' +
                ", departmentType='" + departmentType + '\'' +
                ", ambulanceIncidentNumber='" + ambulanceIncidentNumber + '\'' +
                ", ambulanceTrustOrganisationCode='" + ambulanceTrustOrganisationCode + '\'' +
                ", attendanceIdentifier='" + attendanceIdentifier + '\'' +
                ", arrivalMode='" + arrivalMode + '\'' +
                ", attendanceCategory='" + attendanceCategory + '\'' +
                ", attendanceSource='" + attendanceSource + '\'' +
                ", arrivalDate='" + arrivalDate + '\'' +
                ", initialAssessmentDate='" + initialAssessmentDate + '\'' +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", seenForTreatmentDate='" + seenForTreatmentDate + '\'' +
                ", decidedToAdmitDate='" + decidedToAdmitDate + '\'' +
                ", treatmentFunctionCode='" + treatmentFunctionCode + '\'' +
                ", dischargeStatus='" + dischargeStatus + '\'' +
                ", dischargeDestination='" + dischargeDestination + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", mhClassifications='" + mhClassifications + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", investigations='" + investigations + '\'' +
                ", treatments='" + treatments + '\'' +
                ", referredToServices='" + referredToServices + '\'' +
                ", safeguardingConcerns='" + safeguardingConcerns + '\'' +
                ", lookupPersonId=" + lookupPersonId +
                ", audit=" + audit +
                '}';
    }

}