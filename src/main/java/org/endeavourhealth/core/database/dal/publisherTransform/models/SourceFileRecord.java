package org.endeavourhealth.core.database.dal.publisherTransform.models;

public class SourceFileRecord {

    private Long id;
    private int sourceFileId;
    private String sourceLocation;
    private String[] values;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSourceFileId() {
        return sourceFileId;
    }

    public void setSourceFileId(int sourceFileId) {
        this.sourceFileId = sourceFileId;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
