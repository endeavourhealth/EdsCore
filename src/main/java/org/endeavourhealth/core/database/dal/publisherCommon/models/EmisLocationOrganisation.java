package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class EmisLocationOrganisation {

    private String organisationGuid; 
    private boolean isMainLocation; 
    private boolean organisationLocationDeleted; 
    private int publishedFileId; 
    private int publishedFileRecordNumber;

    public EmisLocationOrganisation() {
    }

    public String getOrganisationGuid() {
        return organisationGuid;
    }

    public void setOrganisationGuid(String organisationGuid) {
        this.organisationGuid = organisationGuid;
    }

    public boolean isMainLocation() {
        return isMainLocation;
    }

    public void setMainLocation(boolean mainLocation) {
        isMainLocation = mainLocation;
    }

    public boolean isOrganisationLocationDeleted() {
        return organisationLocationDeleted;
    }

    public void setOrganisationLocationDeleted(boolean organisationLocationDeleted) {
        this.organisationLocationDeleted = organisationLocationDeleted;
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
