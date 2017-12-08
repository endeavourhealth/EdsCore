package org.endeavourhealth.core.database.rdbms.jdbcreader.models;

import org.endeavourhealth.core.database.dal.jdbcreader.models.Batch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "batch")
public class RdbmsBatch implements Serializable {

    private Long batchId;
    private String configurationId = null;
    private String interfaceTypeName;
    private String batchIdentifier = null;
    private String localPath = null;
    private Date insertDateTimestamp = null;
    private boolean complete;
    private Date completeDate = null;
    private boolean notified;
    private Date notificationDate = null;

    public RdbmsBatch() {}

    public RdbmsBatch(Batch batch) {
        this.batchId = batch.getBatchId();
        this.configurationId = batch.getConfigurationId();
        this.interfaceTypeName = batch.getInterfaceTypeName();
        this.batchIdentifier  = batch.getBatchIdentifier();
        this.localPath = batch.getLocalPath();
        this.insertDateTimestamp  = batch.getInsertDate();
        this.complete = batch.isComplete();
        this.completeDate  = batch.getCompleteDate();
        this.notified = batch.isNotified();
        this.notificationDate  = batch.getNotificationDate();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id", updatable = false, nullable = false)
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    @Column(name = "configuration_id", nullable = false)
    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    @Column(name = "interface_type_name", nullable = false)
    public String getInterfaceTypeName() {
        return interfaceTypeName;
    }

    public void setInterfaceTypeName(String interfaceTypeName) {
        this.interfaceTypeName = interfaceTypeName;
    }

    @Column(name = "batch_identifier", nullable = false)
    public String getBatchIdentifier() {
        return batchIdentifier;
    }

    public void setBatchIdentifier(String batchIdentifier) {
        this.batchIdentifier = batchIdentifier;
    }

    @Column(name = "local_path", nullable = true)
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Column(name = "insert_date", nullable = false)
    public Date getInsertDateTimestamp() {
        return insertDateTimestamp;
    }

    public void setInsertDateTimestamp(Date insertDateTimestamp) {
        this.insertDateTimestamp = insertDateTimestamp;
    }

    @Column(name = "complete", nullable = true)
    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Column(name = "complete_date", nullable = true)
    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Column(name = "notified", nullable = true)
    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Column(name = "notification_date", nullable = true)
    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

}
