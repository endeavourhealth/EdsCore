package org.endeavourhealth.core.database.rdbms.reference.models;

import org.endeavourhealth.core.database.dal.reference.models.MultiLexToCTV3Map;
import org.endeavourhealth.core.database.dal.reference.models.Read2ToSnomedMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "multilex_to_ctv3_map")
public class RdbmsMultiLexToCTV3Map implements Serializable {

    private long rowId;
    private long multiLexProductId;
    private String ctv3ReadCode;
    private String ctv3ReadTerm;

    public RdbmsMultiLexToCTV3Map() {}

    public RdbmsMultiLexToCTV3Map(MultiLexToCTV3Map proxy) {
        this.rowId = proxy.getRowId();
        this.multiLexProductId = proxy.getMultiLexProductId();
        this.ctv3ReadCode = proxy.getCTV3ReadCode();
        this.ctv3ReadTerm = proxy.getCTV3ReadTerm();
    }

    @Id
    @Column(name = "row_id", nullable = false)
    public long getRowId() {
        return rowId;
    }
    public void setRowId(int rowId) { this.rowId = rowId; }

    @Column(name = "multilex_product_id", nullable = false)
    public long getMultiLexProductId() {
        return multiLexProductId;
    }
    public void setMultiLexProductId(long multiLexProductId) {
        this.multiLexProductId = multiLexProductId;
    }

    @Column(name = "ctv3_read_code", nullable = false)
    public String getCTV3ReadCode() {
        return ctv3ReadCode;
    }
    public void setCTV3ReadCode(String ctv3ReadCode) {
        this.ctv3ReadCode = ctv3ReadCode;
    }

    @Column(name = "ctv3_read_term", nullable = false)
    public String getCTV3ReadTerm() {
        return ctv3ReadTerm;
    }
    public void setCTV3ReadTerm(String ctv3ReadTerm) {
        this.ctv3ReadTerm = ctv3ReadTerm;
    }
}

