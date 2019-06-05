package org.endeavourhealth.core.database.rdbms.datagenerator.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "subscriber_zip_file_uuids", schema = "data_generator")
public class RdbmsSubscriberZipFileUUIDs implements Serializable {

    private int subscriberId;
    private String queuedMessageUUID;
    private String queuedMessageBody;
    private long filingOrder;
    private Date fileSent;
    private Date fileFilingAttempted;
    private Boolean fileFilingSuccess;

    @Basic
    @Column(name = "subscriber_id")
    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
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
    public Date getFileSent() {
        return fileSent;
    }

    public void setFileSent(Date fileSent) {
        this.fileSent = fileSent;
    }

    @Basic
    @Column(name = "file_filing_attempted")
    public Date getFileFilingAttempted() {
        return fileFilingAttempted;
    }

    public void setFileFilingAttempted(Date fileFilingAttempted) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsSubscriberZipFileUUIDs that = (RdbmsSubscriberZipFileUUIDs) o;
        return subscriberId == that.subscriberId &&
                Objects.equals(queuedMessageUUID, that.queuedMessageUUID) &&
                Objects.equals(queuedMessageBody, that.queuedMessageBody) &&
                filingOrder == that.filingOrder &&
                Objects.equals(fileSent, that.fileSent) &&
                Objects.equals(fileFilingAttempted, that.fileFilingAttempted) &&
                Objects.equals(fileFilingSuccess, that.fileFilingSuccess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberId, queuedMessageUUID, queuedMessageBody,
                filingOrder, fileSent, fileFilingAttempted, fileFilingSuccess);
    }
}
