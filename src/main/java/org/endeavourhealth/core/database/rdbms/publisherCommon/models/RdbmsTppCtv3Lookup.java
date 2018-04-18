package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppCtv3Lookup;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tpp_ctv3_lookup")
public class RdbmsTppCtv3Lookup {
    private long rowId;
    private String ctv3Code;
    private String ctv3Text;
    private String auditJson;

    public RdbmsTppCtv3Lookup() { }

    public RdbmsTppCtv3Lookup(TppCtv3Lookup proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.ctv3Code = proxy.getCtv3Code();
        this.ctv3Text = proxy.getCtv3Text();

        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
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
    @Column(name = "ctv3_code")
    public String getCtv3Code() {
        return ctv3Code;
    }

    public void setCtv3Code(String ctv3Code) {
        this.ctv3Code = ctv3Code;
    }

    @Basic
    @Column(name = "ctv3_text")
    public String getCtv3Text() {
        return ctv3Text;
    }

    public void setCtv3Text(String ctv3Text) {
        this.ctv3Text = ctv3Text;
    }

    @Basic
    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsTppCtv3Lookup that = (RdbmsTppCtv3Lookup) o;
        return rowId == that.rowId &&
                Objects.equals(ctv3Code, that.ctv3Code) &&
                Objects.equals(ctv3Text, that.ctv3Text) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rowId, ctv3Code, ctv3Text, auditJson);
    }
}
