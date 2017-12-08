package org.endeavourhealth.core.database.rdbms.jdbcreader.models;

import org.endeavourhealth.core.database.dal.jdbcreader.models.NotificationMessage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "notification_message")
public class RdbmsNotificationMessage implements Serializable {

    private Long notificationMessageId;
    private Long batchId;
    private String configurationId = null;
    private String messageUuid = null;
    private Date notificationTimestamp;
    private String outbound = null;
    private String inbound = null;
    private boolean success;
    private String errorText = null;

    public RdbmsNotificationMessage() {}

    public RdbmsNotificationMessage(NotificationMessage proxy) {
        this.notificationMessageId = proxy.getNotificationMessageId();
        this.batchId = proxy.getBatchId();
        this.configurationId  = proxy.getConfigurationId();
        this.messageUuid  = proxy.getMessageUuid();
        this.notificationTimestamp = proxy.getNotificationTimestamp();
        this.outbound  = proxy.getOutbound();
        this.inbound  = proxy.getInbound();
        this.success = proxy.isSuccess();
        this.errorText  = proxy.getErrorText();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_message_id", updatable = false, nullable = false)
    public Long getNotificationMessageId() {
        return notificationMessageId;
    }

    public void setNotificationMessageId(Long notificationMessageId) {
        this.notificationMessageId = notificationMessageId;
    }

    @Column(name = "batch_id", nullable = false)
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

    @Column(name = "message_uuid", nullable = false)
    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    @Column(name = "notification_timestamp", nullable = false)
    public Date getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(Date notificationTimestamp) {
        this.notificationTimestamp = notificationTimestamp;
    }

    @Column(name = "outbound", nullable = false)
    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    @Column(name = "inbound", nullable = true)
    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    @Column(name = "success", nullable = false)
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Column(name = "error_text", nullable = true)
    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }


}
