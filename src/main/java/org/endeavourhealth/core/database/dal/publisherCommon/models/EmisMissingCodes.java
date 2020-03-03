package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class EmisMissingCodes {

    private String serviceId;
    private String exchangeId;
    private String patientGuid;
    private String recordGuid;
    private Long codeId;
    private String fileType;
    private String codeType;

    public EmisMissingCodes() {
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getErrorRecclassName() {
        return errorRecclassName;
    }

    public void setErrorRecclassName(String errorRecclassName) {
        this.errorRecclassName = errorRecclassName;
    }

    private String errorRecclassName;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getPatientGuid() {
        return patientGuid;
    }

    public void setPatientGuid(String patientGuid) {
        this.patientGuid = patientGuid;
    }

    public String getRecordGuid() {
        return recordGuid;
    }

    public void setRecordGuid(String recordGuid) {
        this.recordGuid = recordGuid;
    }

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }
}

