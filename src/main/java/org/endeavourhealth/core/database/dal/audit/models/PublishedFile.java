package org.endeavourhealth.core.database.dal.audit.models;

import java.util.Date;

public class PublishedFile {
    private Integer id;
    private String serviceId;
    private String systemId;
    private String filePath;
    private Date insertedAt;
    private int publishedFileTypeId;
    private String exchangeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    public int getPublishedFileTypeId() {
        return publishedFileTypeId;
    }

    public void setPublishedFileTypeId(int publishedFileTypeId) {
        this.publishedFileTypeId = publishedFileTypeId;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }
}
