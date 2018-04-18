package org.endeavourhealth.core.database.dal.publisherCommon.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppCtv3Lookup;


public class TppCtv3Lookup {
    private long rowId;
    private String ctv3Code;
    private String ctv3Text;
    private ResourceFieldMappingAudit audit = null;

    public TppCtv3Lookup(RdbmsTppCtv3Lookup proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.ctv3Code = proxy.getCtv3Code();
        this.ctv3Text = proxy.getCtv3Text();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppCtv3Lookup(long rowId,
                          String ctv3Code,
                          String ctv3Text,
                          ResourceFieldMappingAudit audit ) {
        this.rowId = rowId;
        this.ctv3Code = ctv3Code;
        this.ctv3Text= ctv3Text;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getCtv3Code() {
        return ctv3Code;
    }

    public void setCtv3Code(String ctv3Code) {
        this.ctv3Code = ctv3Code;
    }

    public String getCtv3Text() {
        return ctv3Text;
    }

    public void setCtv3Text(String ctv3Text) {
        this.ctv3Text = ctv3Text;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
