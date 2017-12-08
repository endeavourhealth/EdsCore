package org.endeavourhealth.core.database.dal.jdbcreader.models;

import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsBatchFile;

import java.util.Date;

public class BatchFile {

    private Long batchFileId;
    private Long batchId;
    private String fileTypeIdentifier = null;
    private Date insertDate;
    private String filename = null;
    private boolean downloaded;
    private Date downloadDate;

    public BatchFile() {}

    public BatchFile(RdbmsBatchFile proxy) {
        this.batchFileId = proxy.getBatchFileId();
        this.batchId = proxy.getBatchId();
        this.fileTypeIdentifier = proxy.getFileTypeIdentifier();
        this.insertDate = proxy.getInsertDate();
        this.filename = proxy.getFilename();
        this.downloaded = proxy.isDownloaded();
        this.downloadDate = proxy.getDownloadDate();
    }

    public Long getBatchFileId() {
        return batchFileId;
    }

    public void setBatchFileId(Long batchFileId) {
        this.batchFileId = batchFileId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getFileTypeIdentifier() {
        return fileTypeIdentifier;
    }

    public void setFileTypeIdentifier(String fileTypeIdentifier) {
        this.fileTypeIdentifier = fileTypeIdentifier;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }
}
