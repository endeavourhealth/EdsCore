package org.endeavourhealth.core.exceptions;

public class CodeNotFoundException extends Exception {
    private String codeValue;
    private Long code;

    public CodeNotFoundException(String message) {
        super(message);
    }

    public CodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeNotFoundException(Long code, CodeType codeType, String message) {
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





