package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppImmunisationContent {

    private int rowId;
    private String name;
    private String content;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RowId = " + rowId + " Name = [" + name + "] Content = [" + content + "]";
    }
}
