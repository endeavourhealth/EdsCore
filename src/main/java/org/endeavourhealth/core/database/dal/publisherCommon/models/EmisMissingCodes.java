package org.endeavourhealth.core.database.dal.publisherCommon.models;

import java.util.UUID;

public class EmisMissingCodes {

    private UUID serviceId;
    private UUID exchangeId;
    private String fileType;
    private String patientGuid;
    private long codeId;
    private String recordGuid;
    private EmisCodeType codeType;

    public EmisMissingCodes() {
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public long getCodeId() {
        return codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public String getRecordGuid() {
        return recordGuid;
    }

    public void setRecordGuid(String recordGuid) {
        this.recordGuid = recordGuid;
    }

    public EmisCodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(EmisCodeType codeType) {
        this.codeType = codeType;
    }
}

