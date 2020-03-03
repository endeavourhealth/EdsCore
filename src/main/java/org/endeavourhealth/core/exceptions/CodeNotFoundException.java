package org.endeavourhealth.core.exceptions;

public class CodeNotFoundException extends Exception {

    private Long codeValue;
    public CodeNotFoundException(String message) {
        super(message);
    }

    public CodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public CodeNotFoundException(Long codeValue, String message) {
        super(message);
        this.codeValue=codeValue;
    }

    public Long getCodeValue() {
        return this.codeValue;
    }
}





