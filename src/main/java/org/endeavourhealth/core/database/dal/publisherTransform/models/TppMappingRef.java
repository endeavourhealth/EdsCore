package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsCernerCodeValueRef;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppMappingRef;

import java.util.Date;

public class TppMappingRef {

    private long rowId;
    private long groupId;
    private String mappedTerm;
    private String serviceId;
    private ResourceFieldMappingAudit audit = null;

    public TppMappingRef() {}

    public TppMappingRef(RdbmsTppMappingRef proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.groupId = proxy.getGroupId();
        this.mappedTerm = proxy.getMappedTerm();
        this.serviceId = proxy.getServiceId();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppMappingRef(long rowId,
                         long groupId,
                         String mappedTerm,
                         String serviceId,
                         ResourceFieldMappingAudit audit) {
        this.rowId = rowId;
        this.groupId = groupId;
        this.mappedTerm = mappedTerm;
        this.serviceId = serviceId;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
