package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppProfileRole;

import java.util.Date;

public class TppProfileRole {

    private long rowId;
    private String roleDescription;
    private ResourceFieldMappingAudit audit = null;

    public TppProfileRole(RdbmsTppProfileRole proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.roleDescription = proxy.getRoleDescription();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppProfileRole(long rowId,
                                  String roleDescription,
                                  ResourceFieldMappingAudit audit ) {
        this.rowId = rowId;
        this.roleDescription = roleDescription;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
