package org.endeavourhealth.core.database.dal.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;

public class TppMultiLexToCtv3Map {
    private long rowId;
    private long multiLexProductId;
    private String ctv3ReadCode;
    private String ctv3ReadTerm;
    private ResourceFieldMappingAudit audit = null;

    public TppMultiLexToCtv3Map() {}

    /*public TppMultiLexToCtv3Map(RdbmsTppMultilexToCtv3Map proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.multiLexProductId = proxy.getMultilexProductId();
        this.ctv3ReadCode = proxy.getCtv3ReadCode();
        this.ctv3ReadTerm = proxy.getCtv3ReadTerm();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }*/

    public TppMultiLexToCtv3Map(long rowId,
                                long multiLexProductId,
                                String ctv3ReadCode,
                                String ctv3ReadTerm,
                                ResourceFieldMappingAudit audit) {
        this.rowId = rowId;
        this.multiLexProductId = multiLexProductId;
        this.ctv3ReadCode = ctv3ReadCode;
        this.ctv3ReadTerm = ctv3ReadTerm;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getMultiLexProductId() {
        return multiLexProductId;
    }

    public void setMultiLexProductId(long multiLexProductId) {
        this.multiLexProductId = multiLexProductId;
    }

    public String getCtv3ReadCode() {
        return ctv3ReadCode;
    }

    public void setCtv3ReadCode(String ctv3ReadCode) {
        this.ctv3ReadCode = ctv3ReadCode;
    }

    public String getCtv3ReadTerm() {
        return ctv3ReadTerm;
    }

    public void setCtv3ReadTerm(String ctv3ReadTerm) {
        this.ctv3ReadTerm = ctv3ReadTerm;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }

    @Override
    public String toString() {
        return "RowId = " + rowId + " ProductId = " + multiLexProductId + " CTV3Code " + ctv3ReadCode + " CTV3Term " + ctv3ReadTerm;
    }
}