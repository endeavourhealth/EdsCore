package org.endeavourhealth.core.database.rdbms.audit.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "published_file_type")
public class RdbmsPublishedFileType implements Serializable {

    private Integer id;
    private String fileType;
    private Character variableColumnDelimiter;
    private Character variableColumnQuote;
    private Character variableColumnEscape;

    public RdbmsPublishedFileType() {}


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "file_type", nullable = false)
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Column(name = "variable_column_delimiter", nullable = true)
    public Character getVariableColumnDelimiter() {
        return variableColumnDelimiter;
    }

    public void setVariableColumnDelimiter(Character variableColumnDelimiter) {
        this.variableColumnDelimiter = variableColumnDelimiter;
    }

    @Column(name = "variable_column_quote", nullable = true)
    public Character getVariableColumnQuote() {
        return variableColumnQuote;
    }

    public void setVariableColumnQuote(Character variableColumnQuote) {
        this.variableColumnQuote = variableColumnQuote;
    }

    @Column(name = "variable_column_escape", nullable = true)
    public Character getVariableColumnEscape() {
        return variableColumnEscape;
    }

    public void setVariableColumnEscape(Character variableColumnEscape) {
        this.variableColumnEscape = variableColumnEscape;
    }
}
