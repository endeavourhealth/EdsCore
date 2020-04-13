package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class EmisOrganisationLocation {

    private String organisationGuid;
    private String locationGuid;
    private boolean isMainLocation;
    private boolean deleted;
    private int publishedFileId;
    private int publishedFileRecordNumber;

    public EmisOrganisationLocation() {
    }

    public String getOrganisationGuid() {
        return organisationGuid;
    }

    public void setOrganisationGuid(String organisationGuid) {
        this.organisationGuid = organisationGuid;
    }

    public String getLocationGuid() {
        return locationGuid;
    }

    public void setLocationGuid(String locationGuid) {
        this.locationGuid = locationGuid;
    }

    public boolean isMainLocation() {
        return isMainLocation;
    }

    public void setMainLocation(boolean mainLocation) {
        isMainLocation = mainLocation;
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
}
