package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppImmunisationContent;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tpp_immunisation_content")
public class RdbmsTppImmunisationContent {
    private long rowId;
    private String name;
    private String content;
    private Timestamp dateDeleted;
    private String serviceId;
    private String auditJson;

    public RdbmsTppImmunisationContent() {}

    public RdbmsTppImmunisationContent(TppImmunisationContent proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.name = proxy.getName();
        this.content = proxy.getContent();
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "date_deleted")
    public Timestamp getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Timestamp dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsTppImmunisationContent that = (RdbmsTppImmunisationContent) o;
        return rowId == that.rowId &&
                Objects.equals(name, that.name) &&
                Objects.equals(content, that.content) &&
                Objects.equals(dateDeleted, that.dateDeleted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rowId, name, content, dateDeleted);
    }

    @Basic
    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "audit_json")
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

}
