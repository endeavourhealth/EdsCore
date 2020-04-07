package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.Date;
import java.util.Map;

public class TppStaffMemberProfileStaging {

    private int rowIdentifier;
    private Date dtLastUpdated;
    private int staffMemberRowIdentifier;
    private Map<String, String> columnData;
    private int publishedFileId;
    private int publishedRecordNumber;

    public int getRowIdentifier() {
        return rowIdentifier;
    }

    public void setRowIdentifier(int rowIdentifier) {
        this.rowIdentifier = rowIdentifier;
    }

    public Date getDtLastUpdated() {
        return dtLastUpdated;
    }

    public void setDtLastUpdated(Date dtLastUpdated) {
        this.dtLastUpdated = dtLastUpdated;
    }

    public int getStaffMemberRowIdentifier() {
        return staffMemberRowIdentifier;
    }

    public void setStaffMemberRowIdentifier(int staffMemberRowIdentifier) {
        this.staffMemberRowIdentifier = staffMemberRowIdentifier;
    }

    public Map<String, String> getColumnData() {
        return columnData;
    }

    public void setColumnData(Map<String, String> columnData) {
        this.columnData = columnData;
    }

    public int getPublishedFileId() {
        return publishedFileId;
    }

    public void setPublishedFileId(int publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    public int getPublishedRecordNumber() {
        return publishedRecordNumber;
    }

    public void setPublishedRecordNumber(int publishedRecordNumber) {
        this.publishedRecordNumber = publishedRecordNumber;
    }
}
