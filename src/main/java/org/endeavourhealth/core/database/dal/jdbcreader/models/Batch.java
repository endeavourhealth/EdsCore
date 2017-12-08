package org.endeavourhealth.core.database.dal.jdbcreader.models;

import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Batch {

    private Long batchId;
    private String configurationId = null;
    private String interfaceTypeName;
    private String batchIdentifier = null;
    private String localPath = null;
    private Date insertDate = null;
    private boolean complete;
    private Date completeDate = null;
    private boolean notified;
    private Date notificationDate = null;

    private List<BatchFile> batchFiles = new ArrayList<>();

    public Batch() {}

    public Batch(RdbmsBatch proxy) {
        this.batchId = proxy.getBatchId();
        this.configurationId = proxy.getConfigurationId();
        this.interfaceTypeName = proxy.getInterfaceTypeName();
        this.batchIdentifier = proxy.getBatchIdentifier();
        this.localPath = proxy.getLocalPath();
        this.insertDate = proxy.getInsertDateTimestamp();
        this.complete = proxy.isComplete();
        this.completeDate = proxy.getCompleteDate();
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public String getInterfaceTypeName() {
        return interfaceTypeName;
    }

    public void setInterfaceTypeName(String interfaceTypeName) {
        this.interfaceTypeName = interfaceTypeName;
    }

    public String getBatchIdentifier() {
        return batchIdentifier;
    }

    public void setBatchIdentifier(String batchIdentifier) {
        this.batchIdentifier = batchIdentifier;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public void addBatchFile(BatchFile batchFile) {
        this.batchFiles.add(batchFile);
    }

    public void setBatchFiles(List<BatchFile> list) {
        this.batchFiles = list;
    }

    public List<BatchFile> getBatchFiles() {
        return this.batchFiles;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }
}
