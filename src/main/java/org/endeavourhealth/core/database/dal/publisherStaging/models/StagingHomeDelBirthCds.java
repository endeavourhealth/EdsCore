package org.endeavourhealth.core.database.dal.publisherStaging.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

import java.util.Date;
import java.util.Objects;

public class StagingHomeDelBirthCds implements Cloneable {

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

    private String birthWeight;
    private String liveOrStillBirthIndicator;
    private String totalPreviousPregnancies;

    private Integer numberOfBabies;
    private Date firstAntenatalAssessmentDate;
    private String antenatalCarePractitioner;
    private String antenatalCarePractice;
    private String deliveryPlaceIntended;
    private String deliveryPlaceChangeReasonCode;
    private String gestationLengthLabourOnset;
    private Date deliveryDate;
    private String motherNhsNumber;

    private Integer lookupPersonId;

    private ResourceFieldMappingAudit audit = null;

    public StagingHomeDelBirthCds() {
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

    public String getBirthWeight() {
        return birthWeight;
    }

    public void setBirthWeight(String birthWeight) {
        this.birthWeight = birthWeight;
    }

    public String getLiveOrStillBirthIndicator() {
        return liveOrStillBirthIndicator;
    }

    public void setLiveOrStillBirthIndicator(String liveOrStillBirthIndicator) {
        this.liveOrStillBirthIndicator = liveOrStillBirthIndicator;
    }

    public String getTotalPreviousPregnancies() {
        return totalPreviousPregnancies;
    }

    public void setTotalPreviousPregnancies(String totalPreviousPregnancies) {
        this.totalPreviousPregnancies = totalPreviousPregnancies;
    }

    public Integer getNumberOfBabies() {
        return numberOfBabies;
    }

    public void setNumberOfBabies(Integer numberOfBabies) {
        this.numberOfBabies = numberOfBabies;
    }

    public Date getFirstAntenatalAssessmentDate() {
        return firstAntenatalAssessmentDate;
    }

    public void setFirstAntenatalAssessmentDate(Date firstAntenatalAssessmentDate) {
        this.firstAntenatalAssessmentDate = firstAntenatalAssessmentDate;
    }

    public String getAntenatalCarePractitioner() {
        return antenatalCarePractitioner;
    }

    public void setAntenatalCarePractitioner(String antenatalCarePractitioner) {
        this.antenatalCarePractitioner = antenatalCarePractitioner;
    }

    public String getAntenatalCarePractice() {
        return antenatalCarePractice;
    }

    public void setAntenatalCarePractice(String antenatalCarePractice) {
        this.antenatalCarePractice = antenatalCarePractice;
    }

    public String getDeliveryPlaceIntended() {
        return deliveryPlaceIntended;
    }

    public void setDeliveryPlaceIntended(String deliveryPlaceIntended) {
        this.deliveryPlaceIntended = deliveryPlaceIntended;
    }

    public String getDeliveryPlaceChangeReasonCode() {
        return deliveryPlaceChangeReasonCode;
    }

    public void setDeliveryPlaceChangeReasonCode(String deliveryPlaceChangeReasonCode) {
        this.deliveryPlaceChangeReasonCode = deliveryPlaceChangeReasonCode;
    }

    public String getGestationLengthLabourOnset() {
        return gestationLengthLabourOnset;
    }

    public void setGestationLengthLabourOnset(String gestationLengthLabourOnset) {
        this.gestationLengthLabourOnset = gestationLengthLabourOnset;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getMotherNhsNumber() {
        return motherNhsNumber;
    }

    public void setMotherNhsNumber(String motherNhsNumber) {
        this.motherNhsNumber = motherNhsNumber;
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

    public StagingHomeDelBirthCds clone() throws CloneNotSupportedException {
        return (StagingHomeDelBirthCds) super.clone();
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
                birthWeight,
                liveOrStillBirthIndicator,
                totalPreviousPregnancies,
                numberOfBabies,
                firstAntenatalAssessmentDate,
                antenatalCarePractitioner,
                antenatalCarePractice,
                deliveryPlaceIntended,
                deliveryPlaceChangeReasonCode,
                gestationLengthLabourOnset,
                deliveryDate,
                motherNhsNumber,
                lookupPersonId);
    }

    @Override
    public String toString() {
        return "StagingHomeDelBirthCds{" +
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
                ", birthWeight='" + birthWeight + '\'' +
                ", liveOrStillBirthIndicator='" + liveOrStillBirthIndicator + '\'' +
                ", totalPreviousPregnancies='" + totalPreviousPregnancies + '\'' +
                ", numberOfBabies='" + numberOfBabies + '\'' +
                ", firstAntenatalAssessmentDate='" + firstAntenatalAssessmentDate + '\'' +
                ", antenatalCarePractitioner='" + antenatalCarePractitioner + '\'' +
                ", antenatalCarePractice='" + antenatalCarePractice + '\'' +
                ", deliveryPlaceIntended='" + deliveryPlaceIntended + '\'' +
                ", deliveryPlaceChangeReasonCode='" + deliveryPlaceChangeReasonCode + '\'' +
                ", gestationLengthLabourOnset='" + gestationLengthLabourOnset + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", motherNhsNumber='" + motherNhsNumber + '\'' +
                ", lookupPersonId=" + lookupPersonId +
                ", audit=" + audit +
                '}';
    }
}