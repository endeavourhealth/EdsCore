package org.endeavourhealth.core.database.dal.publisherCommon.models;

public enum EmisCodeType {

    CLINICAL_CODE("CLINICAL_CODE", "C"),
    DRUG_CODE("DRUG_CODE", "D");

    private final String code;
    private final String codeValue;

    public String getCode() {
        return code;
    }

    public String getCodeValue() {
        return codeValue;
    }

    EmisCodeType(String code, String codeValue) {
        this.code = code;
        this.codeValue = codeValue;
    }
}
