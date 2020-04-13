package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.Date;
import java.util.Set;

public class EmisLocation {
    
    private String locationGuid;
    private String locationName;
    private String locationTypeDescription;
    private String parentLocationGuid;
    private Date openDate;
    private Date closeDate;
    private String mainContactName;
    private String faxNumber;
    private String emailAddress;
    private String phoneNumber;
    private String houseNameFlatNumber;
    private String numberAndStreet;
    private String village;
    private String town;
    private String county;
    private String postcode;
    private boolean deleted;
    private int publishedFileId;
    private int publishedFileRecordNumber;
    private Set<EmisLocationOrganisation> organisations;

    public EmisLocation() {
    }

    public String getLocationGuid() {
        return locationGuid;
    }

    public void setLocationGuid(String locationGuid) {
        this.locationGuid = locationGuid;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationTypeDescription() {
        return locationTypeDescription;
    }

    public void setLocationTypeDescription(String locationTypeDescription) {
        this.locationTypeDescription = locationTypeDescription;
    }

    public String getParentLocationGuid() {
        return parentLocationGuid;
    }

    public void setParentLocationGuid(String parentLocationGuid) {
        this.parentLocationGuid = parentLocationGuid;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getMainContactName() {
        return mainContactName;
    }

    public void setMainContactName(String mainContactName) {
        this.mainContactName = mainContactName;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHouseNameFlatNumber() {
        return houseNameFlatNumber;
    }

    public void setHouseNameFlatNumber(String houseNameFlatNumber) {
        this.houseNameFlatNumber = houseNameFlatNumber;
    }

    public String getNumberAndStreet() {
        return numberAndStreet;
    }

    public void setNumberAndStreet(String numberAndStreet) {
        this.numberAndStreet = numberAndStreet;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getPublishedFileId() {
        return publishedFileId;
    }

    public void setPublishedFileId(int publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    public int getPublishedFileRecordNumber() {
        return publishedFileRecordNumber;
    }

    public void setPublishedFileRecordNumber(int publishedFileRecordNumber) {
        this.publishedFileRecordNumber = publishedFileRecordNumber;
    }

    public Set<EmisLocationOrganisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Set<EmisLocationOrganisation> organisations) {
        this.organisations = organisations;
    }
}
