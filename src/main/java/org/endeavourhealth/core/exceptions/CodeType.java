package org.endeavourhealth.core.exceptions;

public enum CodeType {

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

    CodeType(String code, String codeValue) {
        this.code = code;
        this.codeValue = codeValue;
    }
}
