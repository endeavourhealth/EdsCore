package org.endeavourhealth.core.database.dal.publisherCommon.models;

public class TppMappingRef {

    private int rowId;
    private int groupId;
    private String mappedTerm;

    public TppMappingRef() {}

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMappedTerm() {
        return mappedTerm;
    }

    public void setMappedTerm(String mappedTerm) {
        this.mappedTerm = mappedTerm;
    }

    @Override
    public String toString() {
        return "RowId = " + rowId + " GroupId = " + groupId + " Term = [" + mappedTerm + "]";
    }
}
