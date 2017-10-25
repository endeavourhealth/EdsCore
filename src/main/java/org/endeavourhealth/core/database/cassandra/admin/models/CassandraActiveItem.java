package org.endeavourhealth.core.database.cassandra.admin.models;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.admin.models.ActiveItem;

import java.util.UUID;

@Table(keyspace = "admin", name = "active_item")
public class CassandraActiveItem {
    @PartitionKey
    @Column(name = "id")
    private UUID id;
    @Column(name = "audit_id")
    private UUID auditId;
    @Column(name = "organisation_id")
    private UUID organisationId;
    @Column(name = "item_id")
    private UUID itemId;
    @Column(name = "item_type_id")
    private Integer itemTypeId;
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public CassandraActiveItem() {}

    public CassandraActiveItem(ActiveItem proxy) {
        this.id = proxy.getId();
        this.itemId = proxy.getItemId();
        this.auditId = proxy.getAuditId();
        this.itemTypeId = proxy.getItemTypeId();
        this.isDeleted = proxy.isDeleted();
        this.organisationId = proxy.getOrganisationId();
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuditId() {
        return auditId;
    }

    public void setAuditId(UUID auditId) {
        this.auditId = auditId;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(UUID organisationId) {
        this.organisationId = organisationId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public Integer getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Integer itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}