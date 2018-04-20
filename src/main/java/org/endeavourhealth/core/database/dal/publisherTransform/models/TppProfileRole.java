package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppProfileRole;

public class TppProfileRole {

    private long rowId;
    private String roleDescription;
    private String serviceId;
    private ResourceFieldMappingAudit audit = null;

    public TppProfileRole(RdbmsTppProfileRole proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.roleDescription = proxy.getRoleDescription();
        this.serviceId = proxy.getServiceId();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppProfileRole(long rowId,
                                  String roleDescription,
                                  String serviceId,
                                  ResourceFieldMappingAudit audit ) {
        this.rowId = rowId;
        this.roleDescription = roleDescription;
        this.serviceId = serviceId;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
