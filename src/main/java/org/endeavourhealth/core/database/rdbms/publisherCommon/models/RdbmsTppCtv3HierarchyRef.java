package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3HierarchyRef;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tpp_ctv3_hierarchy_ref")
public class RdbmsTppCtv3HierarchyRef {
    private long rowId;
    private String ctv3ParentReadCode;
    private String ctv3ChildReadCode;
    private int childLevel;

    public RdbmsTppCtv3HierarchyRef() {}

    public RdbmsTppCtv3HierarchyRef(TppCtv3HierarchyRef proxy) {
        this.rowId = proxy.getRowId();
        this.ctv3ParentReadCode = proxy.getCtv3ParentReadCode();
        this.ctv3ChildReadCode = proxy.getCtv3ChildReadCode();
        this.childLevel = proxy.getChildLevel();
    }

    @Id
    @Column(name = "row_id")
    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @Basic
    @Column(name = "ctv3_parent_read_code")
    public String getCtv3ParentReadCode() {
        return ctv3ParentReadCode;
    }

    public void setCtv3ParentReadCode(String ctv3ParentReadCode) {
        this.ctv3ParentReadCode = ctv3ParentReadCode;
    }

    @Basic
    @Column(name = "ctv3_child_read_code")
    public String getCtv3ChildReadCode() {
        return ctv3ChildReadCode;
    }

    public void setCtv3ChildReadCode(String ctv3ChildReadCode) {
        this.ctv3ChildReadCode = ctv3ChildReadCode;
    }

    @Basic
    @Column(name = "child_level")
    public int getChildLevel() {
        return childLevel;
    }

    public void setChildLevel(int childLevel) {
        this.childLevel = childLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsTppCtv3HierarchyRef that = (RdbmsTppCtv3HierarchyRef) o;
        return rowId == that.rowId &&
                childLevel == that.childLevel &&
                Objects.equals(ctv3ParentReadCode, that.ctv3ParentReadCode) &&
                Objects.equals(ctv3ChildReadCode, that.ctv3ChildReadCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rowId, ctv3ParentReadCode, ctv3ChildReadCode, childLevel);
    }
}
