package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCTV3HierarchyRef;

public class CTV3HierarchyRef {

    private long rowId;
    private String ctv3ParentReadCode;
    private String ctv3ChildReadCode;
    private int childLevel;

    public CTV3HierarchyRef() {}

    public CTV3HierarchyRef(RdbmsCTV3HierarchyRef proxy) {
        this.rowId = proxy.getRowId();
        this.ctv3ParentReadCode = proxy.getCTV3ParentReadCode();
        this.ctv3ChildReadCode = proxy.getCTV3ChildReadTerm();
        this.childLevel = proxy.getChildLevel();
    }

    public CTV3HierarchyRef(long rowId,
                            String ctv3ParentReadCode,
                            String ctv3ChildReadCode,
                            int childLevel) {
        this.rowId = rowId;
        this.ctv3ParentReadCode = ctv3ParentReadCode ;
        this.ctv3ChildReadCode = ctv3ChildReadCode;
        this.childLevel = childLevel;
    }

    public long getRowId() {
        return rowId;
    }
    public void setRowId(int rowId) {this.rowId = rowId; }

    public String getCTV3ParentReadCode() {
        return ctv3ParentReadCode;
    }
    public void setCTV3ParentReadCode(String ctv3ParentReadCode) {
        this.ctv3ParentReadCode = ctv3ParentReadCode;
    }

    public String getCTV3ChildReadCode() {
        return ctv3ChildReadCode;
    }
    public void setCTV3ChildReadCode(String ctv3ChildReadCode) {
        this.ctv3ChildReadCode = ctv3ChildReadCode;
    }

    public int getChildLevel() {
        return childLevel;
    }
    public void setChildLevel(int childLevel) {
        this.childLevel = childLevel;
    }

}
