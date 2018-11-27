package org.endeavourhealth.core.database.dal.audit.models;

public class PublishedFileRecord {

    private int publishedFileId;
    private int recordNumber;
    private long byteStart;
    private int byteLength;

    public int getPublishedFileId() {
        return publishedFileId;
    }

    public void setPublishedFileId(int publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public long getByteStart() {
        return byteStart;
    }

    public void setByteStart(long byteStart) {
        this.byteStart = byteStart;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }
}
