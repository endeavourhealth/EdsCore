package org.endeavourhealth.core.database.dal.publisherTransform.models;

import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsMultiLexToCTV3Map;

public class MultiLexToCTV3Map {

    private long rowId;
    private long multiLexProductId;
    private String ctv3ReadCode;
    private String ctv3ReadTerm;

    public MultiLexToCTV3Map() {}

    public MultiLexToCTV3Map(RdbmsMultiLexToCTV3Map proxy) {
        this.rowId = proxy.getRowId();
        this.multiLexProductId = proxy.getMultiLexProductId();
        this.ctv3ReadCode = proxy.getCTV3ReadCode();
        this.ctv3ReadTerm = proxy.getCTV3ReadTerm();
    }

    public MultiLexToCTV3Map(long rowId,
                         long multiLexProductId,
                         String ctv3ReadCode,
                         String ctv3ReadTerm) {
        this.rowId = rowId;
        this.multiLexProductId = multiLexProductId;
        this.ctv3ReadCode = ctv3ReadCode;
        this.ctv3ReadTerm = ctv3ReadTerm;
    }

    public long getRowId() {
        return rowId;
    }
    public void setRowId(int rowId) {this.rowId = rowId; }

    public long getMultiLexProductId() {
        return multiLexProductId;
    }
    public void setMultiLexProductId(long multiLexProductId) {
        this.multiLexProductId = multiLexProductId;
    }

    public String getCTV3ReadCode() {
        return ctv3ReadCode;
    }
    public void setCTV3ReadCode(String ctv3ReadCode) {
        this.ctv3ReadCode = ctv3ReadCode;
    }

    public String getCTV3ReadTerm() {
        return ctv3ReadTerm;
    }
    public void setCTV3ReadTerm(String ctv3ReadTerm) {
        this.ctv3ReadTerm = ctv3ReadTerm;
    }
}
