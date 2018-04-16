package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CTV3HierarchyRef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ctv3_hierarchy_ref")
public class RdbmsCTV3HierarchyRef implements Serializable {

    private long rowId;
    private String ctv3ParentReadCode;
    private String ctv3ChildReadCode;
    private int childLevel;

    public RdbmsCTV3HierarchyRef() {}

    public RdbmsCTV3HierarchyRef(CTV3HierarchyRef proxy) {
        this.rowId = proxy.getRowId();
        this.ctv3ParentReadCode = proxy.getCTV3ParentReadCode();
        this.ctv3ChildReadCode = proxy.getCTV3ChildReadCode();
        this.childLevel = proxy.getChildLevel();
    }

    @Id
    @Column(name = "row_id", nullable = false)
    public long getRowId() {
        return rowId;
    }
    public void setRowId(int rowId) { this.rowId = rowId; }

    @Column(name = "ctv3_parent_read_code", nullable = false)
    public String getCTV3ParentReadCode() {
        return ctv3ParentReadCode;
    }
    public void setCTV3ParentReadCode(String ctv3ParentReadCode) { this.ctv3ParentReadCode = ctv3ParentReadCode; }

    @Column(name = "ctv3_child_read_code", nullable = false)
    public String getCTV3ChildReadTerm() {
        return ctv3ChildReadCode;
    }
    public void setCTV3ChildReadTerm(String ctv3ChildReadCode) {
        this.ctv3ChildReadCode = ctv3ChildReadCode;
    }

    @Column(name = "child_level", nullable = false)
    public int getChildLevel() {
        return childLevel;
    }
    public void setChildLevel(int childLevel) {
        this.childLevel = childLevel;
    }

}

