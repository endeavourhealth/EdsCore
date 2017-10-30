package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.core.database.cassandra.admin.models.CassandraActiveItem;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsActiveItem;

import java.util.UUID;

public class ActiveItem {

    private UUID id = null; //this is ONLY used in Cassandra
    private UUID itemId = null;
    private UUID auditId = null;
    private int itemTypeId = -1;
    private boolean isDeleted = false;
    private UUID organisationId = null;

    public ActiveItem() {}

    public ActiveItem(CassandraActiveItem proxy) {
        this.id = proxy.getId();
        this.itemId = proxy.getItemId();
        this.auditId = proxy.getAuditId();
        this.itemTypeId = proxy.getItemTypeId();
        this.isDeleted = proxy.getIsDeleted();
        this.organisationId = proxy.getOrganisationId();
    }

    public ActiveItem(RdbmsActiveItem proxy) {
        //this.id = //not used in MySQL
        this.itemId = UUID.fromString(proxy.getItemId());
        this.auditId = UUID.fromString(proxy.getAuditId());
        this.itemTypeId = proxy.getItemTypeId();
        this.isDeleted = proxy.getIsDeleted();
        this.organisationId = UUID.fromString(proxy.getOrganisationId());
    }

    public static ActiveItem factoryNew(Item item, UUID organisationUuid, DefinitionItemType itemType) {
        UUID itemUuid = item.getId();
        UUID auditUuid = item.getAuditId();

        if (itemUuid == null) {
            throw new RuntimeException("Cannot create ActiveItem without first saving Item to DB");
        }

        ActiveItem ret = new ActiveItem();
        ret.setId(UUID.randomUUID());
        ret.setOrganisationId(organisationUuid);
        ret.setItemId(itemUuid);
        ret.setAuditId(auditUuid);
        ret.setItemTypeId(itemType.getValue());
        ret.setDeleted(false);

        return ret;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getAuditId() {
        return auditId;
    }

    public void setAuditId(UUID auditId) {
        this.auditId = auditId;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(UUID organisationId) {
        this.organisationId = organisationId;
    }
}
