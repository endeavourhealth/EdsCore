package org.endeavourhealth.core.exceptions;

import org.endeavourhealth.core.database.dal.publisherCommon.models.CodeType;

public class EmisCodeNotFoundException extends Exception {
    private String codeValue;
    private Long code;

    public EmisCodeNotFoundException(String message) {
        super(message);
    }

    public EmisCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmisCodeNotFoundException(Long code, CodeType codeType, String message) {
        super(message);
        this.code = code;
        this.codeValue = codeType.getCodeValue();
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public Long getCode() {
        return this.code;
    }
}





