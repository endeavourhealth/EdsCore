package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.Date;

public class EmisOrganisation {

    private String organisationGuid;
    private String cdb;
    private String organisationName;
    private String odsCode;
    private String parentOrganisationGuid;
    private String ccgOrganisationGuid;
    private String organisationType;
    private Date openDate;
    private Date closeDate;
    private String mainLocationGuid;
    private int publishedFileId;
    private int publishedFileRecordNumber;

    public EmisOrganisation() {
    }

    public String getOrganisationGuid() {
        return organisationGuid;
    }

    public void setOrganisationGuid(String organisationGuid) {
        this.organisationGuid = organisationGuid;
    }

    public String getCdb() {
        return cdb;
    }

    public void setCdb(String cdb) {
        this.cdb = cdb;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOdsCode() {
        return odsCode;
    }

    public void setOdsCode(String odsCode) {
        this.odsCode = odsCode;
    }

    public String getParentOrganisationGuid() {
        return parentOrganisationGuid;
    }

    public void setParentOrganisationGuid(String parentOrganisationGuid) {
        this.parentOrganisationGuid = parentOrganisationGuid;
    }

    public String getCcgOrganisationGuid() {
        return ccgOrganisationGuid;
    }

    public void setCcgOrganisationGuid(String ccgOrganisationGuid) {
        this.ccgOrganisationGuid = ccgOrganisationGuid;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
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

    public String getMainLocationGuid() {
        return mainLocationGuid;
    }

    public void setMainLocationGuid(String mainLocationGuid) {
        this.mainLocationGuid = mainLocationGuid;
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
