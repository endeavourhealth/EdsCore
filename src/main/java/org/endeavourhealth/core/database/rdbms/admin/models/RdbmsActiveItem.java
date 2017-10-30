package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "active_item")
public class RdbmsActiveItem implements Serializable {

    private String itemId = null;
    private String auditId = null;
    private int itemTypeId = -1;
    private boolean isDeleted = false;
    private String organisationId = null;

    public RdbmsActiveItem() {}

    public RdbmsActiveItem(ActiveItem proxy) {
        this.itemId = proxy.getItemId().toString();
        this.auditId = proxy.getAuditId().toString();
        this.itemTypeId = proxy.getItemTypeId();
        this.isDeleted = proxy.isDeleted();
        this.organisationId = proxy.getOrganisationId().toString();
    }

    @Id
    @Column(name = "item_id", nullable = false)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name = "audit_id", nullable = false)
    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    @Column(name = "item_type_id", nullable = false)
    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Column(name = "is_deleted", nullable = true)
    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Column(name = "organisation_id", nullable = true)
    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }
}
