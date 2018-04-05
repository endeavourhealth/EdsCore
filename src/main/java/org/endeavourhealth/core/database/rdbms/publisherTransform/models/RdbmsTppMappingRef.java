package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppMappingRef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tpp_mapping_ref")
public class RdbmsTppMappingRef implements Serializable {
    private long rowId;
    private long groupId;
    private String mappedTerm;
    private String serviceId;
    private String auditJson;

    public RdbmsTppMappingRef() {}

    public RdbmsTppMappingRef(TppMappingRef proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.groupId = proxy.getGroupId();
        this.mappedTerm = proxy.getMappedTerm();
        this.serviceId = proxy.getServiceId();
        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "row_id", nullable = false)
    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @Column(name = "group_id", nullable = false)
    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    @Column(name = "mapped_term", nullable = false)
    public String getMappedTerm() {
        return mappedTerm;
    }

    public void setMappedTerm(String mappedTerm) {
        this.mappedTerm = mappedTerm;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "audit_json", nullable = true)
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
        RdbmsTppMappingRef that = (RdbmsTppMappingRef) o;
        return rowId == that.rowId &&
                groupId == that.groupId &&
                Objects.equals(mappedTerm, that.mappedTerm) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, groupId, mappedTerm, serviceId, auditJson);
    }
}
