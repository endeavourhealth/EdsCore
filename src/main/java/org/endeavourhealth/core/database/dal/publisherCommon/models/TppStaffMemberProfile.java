package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.Date;

public class TppStaffMemberProfile {

    private int rowId;
    private String organisationId;
    private int staffMemberRowId;
    private Date startDate;
    private Date endDate;
    private String roleName;
    private String ppaId;
    private String gpLocalCode;
    private String gmpId;
    private boolean removedData;
    private int publishedFileId;
    private int publishedFileRecordNumber;

    public TppStaffMemberProfile() {
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public int getStaffMemberRowId() {
        return staffMemberRowId;
    }

    public void setStaffMemberRowId(int staffMemberRowId) {
        this.staffMemberRowId = staffMemberRowId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPpaId() {
        return ppaId;
    }

    public void setPpaId(String ppaId) {
        this.ppaId = ppaId;
    }

    public String getGpLocalCode() {
        return gpLocalCode;
    }

    public void setGpLocalCode(String gpLocalCode) {
        this.gpLocalCode = gpLocalCode;
    }

    public String getGmpId() {
        return gmpId;
    }

    public void setGmpId(String gmpId) {
        this.gmpId = gmpId;
    }

    public boolean isRemovedData() {
        return removedData;
    }

    public void setRemovedData(boolean removedData) {
        this.removedData = removedData;
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
