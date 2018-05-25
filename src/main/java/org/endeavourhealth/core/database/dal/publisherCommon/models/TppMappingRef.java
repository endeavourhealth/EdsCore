package org.endeavourhealth.core.database.dal.publisherCommon.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsTppMappingRef;

public class TppMappingRef {

    private long rowId;
    private long groupId;
    private String mappedTerm;
    private ResourceFieldMappingAudit audit = null;

    public TppMappingRef() {}

    public TppMappingRef(RdbmsTppMappingRef proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.groupId = proxy.getGroupId();
        this.mappedTerm = proxy.getMappedTerm();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppMappingRef(long rowId,
                         long groupId,
                         String mappedTerm,
                         ResourceFieldMappingAudit audit) {
        this.rowId = rowId;
        this.groupId = groupId;
        this.mappedTerm = mappedTerm;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getGroupId() { return groupId; }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getMappedTerm() {
        return mappedTerm;
    }

    public void setMappedTerm(String mappedTerm) {
        this.mappedTerm = mappedTerm;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
