package org.endeavourhealth.core.database.dal.publisherCommon.models;


public class TppConfigListOption {

    private int rowId;
    private int configListId;
    private String listOptionName;

    public TppConfigListOption() {}

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getConfigListId() {
        return configListId;
    }

    public void setConfigListId(int configListId) {
        this.configListId = configListId;
    }

    public String getListOptionName() {
        return listOptionName;
    }

    public void setListOptionName(String listOptionName) {
        this.listOptionName = listOptionName;
    }

    @Override
    public String toString() {
        return "RowId = " + rowId + " Name = [" + listOptionName + "]";
    }
}
