package org.endeavourhealth.core.database.dal.audit.models;

import java.util.UUID;

public class TransformWarning {

    private UUID serviceId;
    private UUID systemId;
    private UUID exchangeId;
    private Integer publishedFileId;
    private Integer recordNumber;
    private String warningText;
    private String[] warningParams;

    public TransformWarning() {}

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Integer getPublishedFileId() {
        return publishedFileId;
    }

    public void setPublishedFileId(Integer publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getWarningText() {
        return warningText;
    }

    public void setWarningText(String warningText) {
        this.warningText = warningText;
    }

    public String[] getWarningParams() {
        return warningParams;
    }

    public void setWarningParams(String[] warningParams) {
        this.warningParams = warningParams;
    }
}
