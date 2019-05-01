package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transform_warning")
public class RdbmsTransformWarning {

    private int id;
    private String serviceId;
    private String systemId;
    private String exchangeId;
    private Long sourceFileRecordId;
    private Date insertedAt;
    private int transformWarningTypeId;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private Integer publishedFileId;
    private Integer recordNumber;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Column(name = "exchange_id", nullable = false)
    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Column(name = "source_file_record_id", nullable = true)
    public Long getSourceFileRecordId() {
        return sourceFileRecordId;
    }

    public void setSourceFileRecordId(Long sourceFileRecordId) {
        this.sourceFileRecordId = sourceFileRecordId;
    }

    @Column(name = "inserted_at", nullable = false)
    public Date getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(Date insertedAt) {
        this.insertedAt = insertedAt;
    }

    @Column(name = "transform_warning_type_id", nullable = false)
    public int getTransformWarningTypeId() {
        return transformWarningTypeId;
    }

    public void setTransformWarningTypeId(int transformWarningTypeId) {
        this.transformWarningTypeId = transformWarningTypeId;
    }

    @Column(name = "param_1", nullable = true)
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        if (param1.length() > 255) {
            param1 = param1.substring(0, 254);
        }
        this.param1 = param1;
    }

    @Column(name = "param_2", nullable = true)
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        if (param2.length() > 255) {
            param2 = param2.substring(0, 254);
        }
        this.param2 = param2;
    }

    @Column(name = "param_3", nullable = true)
    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        if (param3.length() > 255) {
            param3 = param3.substring(0, 254);
        }
        this.param3 = param3;
    }

    @Column(name = "param_4", nullable = true)
    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        if (param4.length() > 255) {
            param4 = param4.substring(0, 254);
        }
        this.param4 = param4;
    }

    @Column(name = "published_file_id", nullable = true)
    public Integer getPublishedFileId() {
        return publishedFileId;
    }

    public void setPublishedFileId(Integer publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    @Column(name = "record_number", nullable = true)
    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }
}
