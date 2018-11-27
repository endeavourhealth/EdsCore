package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "published_file")
public class RdbmsPublishedFile implements Serializable {

    private Integer id;
    private String serviceId;
    private String systemId;
    private String filePath;
    private Date insertedAt;
    private int publishedFileTypeId;
    private String exchangeId;

    public RdbmsPublishedFile() {}

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "system_id", nullable = false)
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Column(name = "file_path", nullable = false)
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "inserted_at", nullable = false)
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Column(name = "published_file_type_id", nullable = false)
    public int getPublishedFileTypeId() {
        return publishedFileTypeId;
    }

    public void setPublishedFileTypeId(int publishedFileTypeId) {
        this.publishedFileTypeId = publishedFileTypeId;
    }

    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }
}
