package org.endeavourhealth.core.database.rdbms.usermanager.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "audit", schema = "user_manager")
@IdClass(AuditEntityPK.class)
public class AuditEntity {
    private String id;
    private String userProjectId;
    private Timestamp timestamp;
    private Byte auditType;
    private String itemBefore;
    private String itemAfter;
    private Byte itemType;
    private String auditJson;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "user_project_id")
    public String getUserProjectId() {
        return userProjectId;
    }

    public void setUserProjectId(String userProjectId) {
        this.userProjectId = userProjectId;
    }

    @Id
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "audit_type")
    public Byte getAuditType() {
        return auditType;
    }

    public void setAuditType(Byte auditType) {
        this.auditType = auditType;
    }

    @Basic
    @Column(name = "item_before")
    public String getItemBefore() {
        return itemBefore;
    }

    public void setItemBefore(String itemBefore) {
        this.itemBefore = itemBefore;
    }

    @Basic
    @Column(name = "item_after")
    public String getItemAfter() {
        return itemAfter;
    }

    public void setItemAfter(String itemAfter) {
        this.itemAfter = itemAfter;
    }

    @Basic
    @Column(name = "item_type")
    public Byte getItemType() {
        return itemType;
    }

    public void setItemType(Byte itemType) {
        this.itemType = itemType;
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
        AuditEntity that = (AuditEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userProjectId, that.userProjectId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(auditType, that.auditType) &&
                Objects.equals(itemBefore, that.itemBefore) &&
                Objects.equals(itemAfter, that.itemAfter) &&
                Objects.equals(itemType, that.itemType) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userProjectId, timestamp, auditType, itemBefore, itemAfter, itemType, auditJson);
    }

}
