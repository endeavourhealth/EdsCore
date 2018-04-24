package org.endeavourhealth.core.database.dal.publisherCommon.models;

import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppCtv3HierarchyRef;

public class TppCtv3HierarchyRef {

    private long rowId;
    private String ctv3ParentReadCode;
    private String ctv3ChildReadCode;
    private int childLevel;

    public TppCtv3HierarchyRef() {}

    public TppCtv3HierarchyRef(RdbmsTppCtv3HierarchyRef proxy) {
        this.rowId = proxy.getRowId();
        this.ctv3ParentReadCode = proxy.getCtv3ParentReadCode();
        this.ctv3ChildReadCode = proxy.getCtv3ChildReadCode();
        this.childLevel = proxy.getChildLevel();
    }

    public TppCtv3HierarchyRef(long rowId,
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

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getCtv3ParentReadCode() {
        return ctv3ParentReadCode;
    }

    public void setCtv3ParentReadCode(String ctv3ParentReadCode) {
        this.ctv3ParentReadCode = ctv3ParentReadCode;
    }

    public String getCtv3ChildReadCode() {
        return ctv3ChildReadCode;
    }

    public void setCtv3ChildReadCode(String ctv3ChildReadCode) {
        this.ctv3ChildReadCode = ctv3ChildReadCode;
    }

    public int getChildLevel() {
        return childLevel;
    }

    public void setChildLevel(int childLevel) {
        this.childLevel = childLevel;
    }
}
