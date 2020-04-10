package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppStaffMember {

    private int rowId;
    private String staffName;
    private String username;
    private String nationalIdType;
    private String nationalId;
    private String smartcardId;
    private int publishedFileId;
    private int publishedFileRecordNumber;


    public TppStaffMember() {
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNationalIdType() {
        return nationalIdType;
    }

    public void setNationalIdType(String nationalIdType) {
        this.nationalIdType = nationalIdType;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getSmartcardId() {
        return smartcardId;
    }

    public void setSmartcardId(String smartcardId) {
        this.smartcardId = smartcardId;
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
