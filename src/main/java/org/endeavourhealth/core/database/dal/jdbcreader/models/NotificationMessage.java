package org.endeavourhealth.core.database.dal.jdbcreader.models;

import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsNotificationMessage;

import java.util.Date;

public class NotificationMessage {

    private Long notificationMessageId;
    private Long batchId;
    private String configurationId = null;
    private String messageUuid = null;
    private Date notificationTimestamp;
    private String outbound = null;
    private String inbound = null;
    private boolean success;
    private String errorText = null;

    public NotificationMessage() {}

    public NotificationMessage(RdbmsNotificationMessage proxy) {
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

    public Long getNotificationMessageId() {
        return notificationMessageId;
    }

    public void setNotificationMessageId(Long notificationMessageId) {
        this.notificationMessageId = notificationMessageId;
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

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public Date getNotificationTimestamp() {
        return notificationTimestamp;
    }

    public void setNotificationTimestamp(Date notificationTimestamp) {
        this.notificationTimestamp = notificationTimestamp;
    }

    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

}
