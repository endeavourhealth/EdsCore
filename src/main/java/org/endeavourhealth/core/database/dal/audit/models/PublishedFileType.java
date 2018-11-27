package org.endeavourhealth.core.database.dal.audit.models;

import java.util.ArrayList;
import java.util.List;

public class PublishedFileType {

    private String fileType;
    private Character variableColumnDelimiter;
    private Character variableColumnQuote;
    private Character variableColumnEscape;
    private List<PublishedFileColumn> columns = new ArrayList<>();

    public PublishedFileType() {}

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Character getVariableColumnDelimiter() {
        return variableColumnDelimiter;
    }

    public void setVariableColumnDelimiter(Character variableColumnDelimiter) {
        this.variableColumnDelimiter = variableColumnDelimiter;
    }

    public Character getVariableColumnQuote() {
        return variableColumnQuote;
    }

    public void setVariableColumnQuote(Character variableColumnQuote) {
        this.variableColumnQuote = variableColumnQuote;
    }

    public Character getVariableColumnEscape() {
        return variableColumnEscape;
    }

    public void setVariableColumnEscape(Character variableColumnEscape) {
        this.variableColumnEscape = variableColumnEscape;
    }

    public List<PublishedFileColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<PublishedFileColumn> columns) {
        this.columns = columns;
    }
}
