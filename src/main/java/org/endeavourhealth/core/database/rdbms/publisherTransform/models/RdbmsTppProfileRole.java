package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppProfileRole;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tpp_profile_role")
public class RdbmsTppProfileRole {
    private long rowId;
    private String roleDescription;
    private String auditJson;
    private String serviceId;

    public RdbmsTppProfileRole() {}

    public RdbmsTppProfileRole(TppProfileRole proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.roleDescription = proxy.getRoleDescription();
        this.serviceId = proxy.getServiceId();
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
    @Column(name = "role_description")
    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
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
        RdbmsTppProfileRole that = (RdbmsTppProfileRole) o;
        return rowId == that.rowId &&
                Objects.equals(roleDescription, that.roleDescription) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rowId, roleDescription, auditJson);
    }

    @Basic
    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
