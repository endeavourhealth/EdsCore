package org.endeavourhealth.core.database.rdbms.jdbcreader.models;

import org.endeavourhealth.core.database.dal.jdbcreader.models.BatchFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "batch_file")
public class RdbmsBatchFile implements Serializable {

    private Long batchFileId;
    private Long batchId;
    private String fileTypeIdentifier = null;
    private Date insertDate;
    private String filename = null;
    private boolean downloaded;
    private Date downloadDate;

    public RdbmsBatchFile() {}
    public RdbmsBatchFile(BatchFile proxy) {
        this.batchFileId = proxy.getBatchFileId();
        this.batchId = proxy.getBatchId();
        this.fileTypeIdentifier  = proxy.getFileTypeIdentifier();
        this.insertDate = proxy.getInsertDate();
        this.filename  = proxy.getFilename();
        this.downloaded = proxy.isDownloaded();
        this.downloadDate = proxy.getDownloadDate();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_file_id", updatable = false, nullable = false)
    public Long getBatchFileId() {
        return batchFileId;
    }

    public void setBatchFileId(Long batchFileId) {
        this.batchFileId = batchFileId;
    }

    @Column(name = "batch_id", nullable = false)
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    @Column(name = "file_type_identifier", nullable = false)
    public String getFileTypeIdentifier() {
        return fileTypeIdentifier;
    }

    public void setFileTypeIdentifier(String fileTypeIdentifier) {
        this.fileTypeIdentifier = fileTypeIdentifier;
    }

    @Column(name = "insert_date", nullable = false)
    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    @Column(name = "filename", nullable = false)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Column(name = "downloaded", nullable = true)
    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    @Column(name = "download_date", nullable = true)
    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }
}
