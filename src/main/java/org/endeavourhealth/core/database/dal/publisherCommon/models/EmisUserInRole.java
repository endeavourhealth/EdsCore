package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.Date;

public class EmisUserInRole {

    private String userInRoleGuid;
    private String organisationGuid;
    private String title;
    private String givenName;
    private String surname;
    private String jobCategoryCode;
    private String jobCategoryName;
    private Date contractStartDate;
    private Date contractEndDate;
    private int publishedFileId;
    private int publishedFileRecordNumber;

    public EmisUserInRole() {
    }

    public String getUserInRoleGuid() {
        return userInRoleGuid;
    }

    public void setUserInRoleGuid(String userInRoleGuid) {
        this.userInRoleGuid = userInRoleGuid;
    }

    public String getOrganisationGuid() {
        return organisationGuid;
    }

    public void setOrganisationGuid(String organisationGuid) {
        this.organisationGuid = organisationGuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getJobCategoryCode() {
        return jobCategoryCode;
    }

    public void setJobCategoryCode(String jobCategoryCode) {
        this.jobCategoryCode = jobCategoryCode;
    }

    public String getJobCategoryName() {
        return jobCategoryName;
    }

    public void setJobCategoryName(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
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
