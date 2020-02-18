package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;

public class StagingCriticalCareCdsTarget {

    private String exchangeId;
    private String uniqueId;
    private boolean isDeleted;
    private Integer personId;
    private Integer performerPersonnelId;
    private String organisationCode;

    private String criticalCareTypeId;
    private String spellNumber;
    private String episodeNumber;
    private String criticalCareIdentifier;
    private Date careStartDate;
    private String careUnitFunction;
    private String admissionSourceCode;
    private String admissionTypeCode;
    private String admissionLocation;

    private String  gestationLengthAtDelivery;
    private Integer advancedRespiratorySupportDays;
    private Integer basicRespiratorySupportsDays;
    private Integer advancedCardiovascularSupportDays;
    private Integer basicCardiovascularSupportDays;
    private Integer renalSupportDays;
    private Integer neurologicalSupportDays;
    private Integer gastroIntestinalSupportDays;
    private Integer dermatologicalSupportDays;
    private Integer liverSupportDays;
    private Integer organSupportMaximum;
    private Integer criticalCareLevel2Days;
    private Integer criticalCareLevel3Days;

    private Date dischargeDate;
    private Date dischargeReadyDate;
    private String dischargeStatusCode;
    private String dischargeDestination;
    private String dischargeLocation;

    private String careActivity1;
    private String careActivity2100;

    private Boolean isConfidential;
    private ResourceFieldMappingAudit audit = null;

    public StagingCriticalCareCdsTarget() {}

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

   public Integer getPerformerPersonnelId() {
        return performerPersonnelId;
    }

    public void setPerformerPersonnelId(Integer performerPersonnelId) {
        this.performerPersonnelId = performerPersonnelId;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public String getCriticalCareTypeId() {
        return criticalCareTypeId;
    }

    public void setCriticalCareTypeId(String criticalCareTypeId) {
        this.criticalCareTypeId = criticalCareTypeId;
    }

    public String getSpellNumber() {
        return spellNumber;
    }

    public void setSpellNumber(String spellNumber) {
        this.spellNumber = spellNumber;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getCriticalCareIdentifier() {
        return criticalCareIdentifier;
    }

    public void setCriticalCareIdentifier(String criticalCareIdentifier) {
        this.criticalCareIdentifier = criticalCareIdentifier;
    }

    public Date getCareStartDate() {
        return careStartDate;
    }

    public void setCareStartDate(Date careStartDate) {
        this.careStartDate = careStartDate;
    }

    public String getCareUnitFunction() {
        return careUnitFunction;
    }

    public void setCareUnitFunction(String careUnitFunction) {
        this.careUnitFunction = careUnitFunction;
    }

    public String getAdmissionSourceCode() {
        return admissionSourceCode;
    }

    public void setAdmissionSourceCode(String admissionSourceCode) {
        this.admissionSourceCode = admissionSourceCode;
    }

    public String getAdmissionTypeCode() {
        return admissionTypeCode;
    }

    public void setAdmissionTypeCode(String admissionTypeCode) {
        this.admissionTypeCode = admissionTypeCode;
    }

    public String getAdmissionLocation() {
        return admissionLocation;
    }

    public void setAdmissionLocation(String admissionLocation) {
        this.admissionLocation = admissionLocation;
    }

    public String getGestationLengthAtDelivery() {
        return gestationLengthAtDelivery;
    }

    public void setGestationLengthAtDelivery(String gestationLengthAtDelivery) {
        this.gestationLengthAtDelivery = gestationLengthAtDelivery;
    }

    public Integer getAdvancedRespiratorySupportDays() {
        return advancedRespiratorySupportDays;
    }

    public void setAdvancedRespiratorySupportDays(Integer advancedRespiratorySupportDays) {
        this.advancedRespiratorySupportDays = advancedRespiratorySupportDays;
    }

    public Integer getBasicRespiratorySupportsDays() {
        return basicRespiratorySupportsDays;
    }

    public void setBasicRespiratorySupportsDays(Integer basicRespiratorySupportsDays) {
        this.basicRespiratorySupportsDays = basicRespiratorySupportsDays;
    }

    public Integer getAdvancedCardiovascularSupportDays() {
        return advancedCardiovascularSupportDays;
    }

    public void setAdvancedCardiovascularSupportDays(Integer advancedCardiovascularSupportDays) {
        this.advancedCardiovascularSupportDays = advancedCardiovascularSupportDays;
    }

    public Integer getBasicCardiovascularSupportDays() {
        return basicCardiovascularSupportDays;
    }

    public void setBasicCardiovascularSupportDays(Integer basicCardiovascularSupportDays) {
        this.basicCardiovascularSupportDays = basicCardiovascularSupportDays;
    }

    public Integer getRenalSupportDays() {
        return renalSupportDays;
    }

    public void setRenalSupportDays(Integer renalSupportDays) {
        this.renalSupportDays = renalSupportDays;
    }

    public Integer getNeurologicalSupportDays() {
        return neurologicalSupportDays;
    }

    public void setNeurologicalSupportDays(Integer neurologicalSupportDays) {
        this.neurologicalSupportDays = neurologicalSupportDays;
    }

    public Integer getGastroIntestinalSupportDays() {
        return gastroIntestinalSupportDays;
    }

    public void setGastroIntestinalSupportDays(Integer gastroIntestinalSupportDays) {
        this.gastroIntestinalSupportDays = gastroIntestinalSupportDays;
    }

    public Integer getDermatologicalSupportDays() {
        return dermatologicalSupportDays;
    }

    public void setDermatologicalSupportDays(Integer dermatologicalSupportDays) {
        this.dermatologicalSupportDays = dermatologicalSupportDays;
    }

    public Integer getLiverSupportDays() {
        return liverSupportDays;
    }

    public void setLiverSupportDays(Integer liverSupportDays) {
        this.liverSupportDays = liverSupportDays;
    }

    public Integer getOrganSupportMaximum() {
        return organSupportMaximum;
    }

    public void setOrganSupportMaximum(Integer organSupportMaximum) {
        this.organSupportMaximum = organSupportMaximum;
    }

    public Integer getCriticalCareLevel2Days() {
        return criticalCareLevel2Days;
    }

    public void setCriticalCareLevel2Days(Integer criticalCareLevel2Days) {
        this.criticalCareLevel2Days = criticalCareLevel2Days;
    }

    public Integer getCriticalCareLevel3Days() {
        return criticalCareLevel3Days;
    }

    public void setCriticalCareLevel3Days(Integer criticalCareLevel3Days) {
        this.criticalCareLevel3Days = criticalCareLevel3Days;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Date getDischargeReadyDate() {
        return dischargeReadyDate;
    }

    public void setDischargeReadyDate(Date dischargeReadyDate) {
        this.dischargeReadyDate = dischargeReadyDate;
    }

    public String getDischargeStatusCode() {
        return dischargeStatusCode;
    }

    public void setDischargeStatusCode(String dischargeStatusCode) {
        this.dischargeStatusCode = dischargeStatusCode;
    }

    public String getDischargeDestination() {
        return dischargeDestination;
    }

    public void setDischargeDestination(String dischargeDestination) {
        this.dischargeDestination = dischargeDestination;
    }

    public String getDischargeLocation() {
        return dischargeLocation;
    }

    public void setDischargeLocation(String dischargeLocation) {
        this.dischargeLocation = dischargeLocation;
    }

    public String getCareActivity1() {
        return careActivity1;
    }

    public void setCareActivity1(String careActivity1) {
        this.careActivity1 = careActivity1;
    }

    public String getCareActivity2100() {
        return careActivity2100;
    }

    public void setCareActivity2100(String careActivity2100) {
        this.careActivity2100 = careActivity2100;
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
        return "StagingCriticalCareCdsTarget{" +
                "exchangeId='" + exchangeId + '\'' +
                ", uniqueId=" + uniqueId +
                ", isDeleted=" + isDeleted +
                ", personId=" + personId +
                ", performerPersonnelId='" + performerPersonnelId + '\'' +
                ", organisationCode='" + organisationCode + '\'' +
                ", criticalCareTypeId='" + criticalCareTypeId + '\'' +
                ", spellNumber='" + spellNumber + '\'' +
                ", episodeNumber='" + episodeNumber + '\'' +
                ", criticalCareIdentifier='" + criticalCareIdentifier + '\'' +
                ", careStartDate='" + careStartDate + '\'' +
                ", careUnitFunction='" + careUnitFunction + '\'' +
                ", admissionSourceCode='" + admissionSourceCode + '\'' +
                ", admissionTypeCode='" + admissionTypeCode + '\'' +
                ", admissionLocation='" + admissionLocation + '\'' +
                ", gestationLengthAtDelivery='" + gestationLengthAtDelivery + '\'' +
                ", advancedRespiratorySupportDays='" + advancedRespiratorySupportDays + '\'' +
                ", basicRespiratorySupportsDays='" + basicRespiratorySupportsDays + '\'' +
                ", advancedCardiovascularSupportDays='" + advancedCardiovascularSupportDays + '\'' +
                ", basicCardiovascularSupportDays='" + basicCardiovascularSupportDays + '\'' +
                ", renalSupportDays='" + renalSupportDays + '\'' +
                ", neurologicalSupportDays='" + neurologicalSupportDays + '\'' +
                ", gastroIntestinalSupportDays='" + gastroIntestinalSupportDays + '\'' +
                ", dermatologicalSupportDays='" + dermatologicalSupportDays + '\'' +
                ", liverSupportDays='" + liverSupportDays + '\'' +
                ", organSupportMaximum='" + organSupportMaximum + '\'' +
                ", criticalCareLevel2Days='" + criticalCareLevel2Days + '\'' +
                ", criticalCareLevel3Days='" + criticalCareLevel3Days + '\'' +
                ", dischargeDate='" + dischargeDate + '\'' +
                ", dischargeReadyDate='" + dischargeReadyDate + '\'' +
                ", dischargeStatusCode='" + dischargeStatusCode + '\'' +
                ", dischargeDestination='" + dischargeDestination + '\'' +
                ", dischargeLocation='" + dischargeLocation + '\'' +
                ", careActivity1='" + careActivity1 + '\'' +
                ", careActivity2100='" + careActivity2100 + '\'' +
                ", audit=" + audit +
                ", isConfidential=" + isConfidential +
                '}';
    }


}