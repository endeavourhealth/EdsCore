package org.endeavourhealth.core.database.rdbms.datagenerator.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "subscriber_zip_file_uuids", schema = "data_generator")
public class RdbmsSubscriberZipFileUUIDs implements Serializable {

    private int subscriberId;
    private String batchUUID;
    private String queuedMessageUUID;
    private String queuedMessageBody;
    private long filingOrder;
    private Timestamp fileSent;
    private Timestamp fileFilingAttempted;
    private Boolean fileFilingSuccess;
    private String filingFailureMessage;

    @Basic
    @Column(name = "subscriber_id")
    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Basic
    @Column(name = "batch_uuid")
    public String getBatchUUID() {
        return batchUUID;
    }

    public void setBatchUUID(String batchUUID) {
        this.batchUUID = batchUUID;
    }

    @Id
    @Column(name = "queued_message_uuid")
    public String getQueuedMessageUUID() {
        return queuedMessageUUID;
    }

    public void setQueuedMessageUUID(String queuedMessageUUID) {
        this.queuedMessageUUID = queuedMessageUUID;
    }

    @Basic
    @Column(name = "queued_message_body")
    public String getQueuedMessageBody() {
        return queuedMessageBody;
    }

    public void setQueuedMessageBody(String queuedMessageBody) {
        this.queuedMessageBody = queuedMessageBody;
    }

    @Basic
    @Column(name = "filing_order")
    public long getFilingOrder() {
        return filingOrder;
    }

    public void setFilingOrder(long filingOrder) {
        this.filingOrder = filingOrder;
    }

    @Basic
    @Column(name = "file_sent")
    public Timestamp getFileSent() {
        return fileSent;
    }

    public void setFileSent(Timestamp fileSent) {
        this.fileSent = fileSent;
    }

    @Basic
    @Column(name = "file_filing_attempted")
    public Timestamp getFileFilingAttempted() {
        return fileFilingAttempted;
    }

    public void setFileFilingAttempted(Timestamp fileFilingAttempted) {
        this.fileFilingAttempted = fileFilingAttempted;
    }

    @Basic
    @Column(name = "file_filing_success")
    public Boolean getFileFilingSuccess() {
        return fileFilingSuccess;
    }

    public void setFileFilingSuccess(Boolean fileFilingSuccess) {
        this.fileFilingSuccess = fileFilingSuccess;
    }

    @Basic
    @Column(name = "filing_failure_message")
    public String getFilingFailureMessage() {
        return filingFailureMessage;
    }

    public void setFilingFailureMessage(String filingFailureMessage) {
        this.filingFailureMessage = filingFailureMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsSubscriberZipFileUUIDs that = (RdbmsSubscriberZipFileUUIDs) o;
        return subscriberId == that.subscriberId &&
                Objects.equals(batchUUID, that.batchUUID) &&
                Objects.equals(queuedMessageUUID, that.queuedMessageUUID) &&
                Objects.equals(queuedMessageBody, that.queuedMessageBody) &&
                filingOrder == that.filingOrder &&
                Objects.equals(fileSent, that.fileSent) &&
                Objects.equals(fileFilingAttempted, that.fileFilingAttempted) &&
                Objects.equals(fileFilingSuccess, that.fileFilingSuccess) &&
                Objects.equals(filingFailureMessage, that.filingFailureMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberId, batchUUID, queuedMessageUUID, queuedMessageBody,
                filingOrder, fileSent, fileFilingAttempted, fileFilingSuccess, filingFailureMessage);
    }
}
