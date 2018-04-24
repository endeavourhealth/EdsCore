package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tpp_multilex_to_ctv3_map")
public class RdbmsTppMultilexToCtv3Map {
    private long rowId;
    private long multilexProductId;
    private String ctv3ReadCode;
    private String ctv3ReadTerm;
    private String auditJson;

    public RdbmsTppMultilexToCtv3Map() {}

    public RdbmsTppMultilexToCtv3Map(TppMultiLexToCtv3Map proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.multilexProductId = proxy.getMultiLexProductId();
        this.ctv3ReadCode = proxy.getCtv3ReadCode();
        this.ctv3ReadTerm = proxy.getCtv3ReadTerm();
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
    @Column(name = "multilex_product_id")
    public long getMultilexProductId() {
        return multilexProductId;
    }

    public void setMultilexProductId(long multilexProductId) {
        this.multilexProductId = multilexProductId;
    }

    @Basic
    @Column(name = "ctv3_read_code")
    public String getCtv3ReadCode() {
        return ctv3ReadCode;
    }

    public void setCtv3ReadCode(String ctv3ReadCode) {
        this.ctv3ReadCode = ctv3ReadCode;
    }

    @Basic
    @Column(name = "ctv3_read_term")
    public String getCtv3ReadTerm() {
        return ctv3ReadTerm;
    }

    public void setCtv3ReadTerm(String ctv3ReadTerm) {
        this.ctv3ReadTerm = ctv3ReadTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsTppMultilexToCtv3Map that = (RdbmsTppMultilexToCtv3Map) o;
        return rowId == that.rowId &&
                multilexProductId == that.multilexProductId &&
                Objects.equals(ctv3ReadCode, that.ctv3ReadCode) &&
                Objects.equals(ctv3ReadTerm, that.ctv3ReadTerm);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rowId, multilexProductId, ctv3ReadCode, ctv3ReadTerm);
    }

    @Basic
    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }
}
