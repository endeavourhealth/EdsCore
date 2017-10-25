package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.core.database.cassandra.admin.models.CassandraItemDependency;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsItemDependency;

import java.util.UUID;

public class ItemDependency {

    private UUID itemId = null;
    private UUID auditId = null;
    private UUID dependentItemId = null;
    private int dependencyTypeId = -1;

    public ItemDependency() {}

    public ItemDependency(CassandraItemDependency proxy) {
        this.itemId = proxy.getItemId();
        this.auditId = proxy.getAuditId();
        this.dependentItemId = proxy.getDependentItemId();
        this.dependencyTypeId = proxy.getDependencyTypeId();
    }

    public ItemDependency(RdbmsItemDependency proxy) {
        this.itemId = UUID.fromString(proxy.getItemId());
        this.auditId = UUID.fromString(proxy.getAuditId());
        this.dependentItemId = UUID.fromString(proxy.getDependentItemId());
        this.dependencyTypeId = proxy.getDependencyTypeId();
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

    public UUID getDependentItemId() {
        return dependentItemId;
    }

    public void setDependentItemId(UUID dependentItemId) {
        this.dependentItemId = dependentItemId;
    }

    public int getDependencyTypeId() {
        return dependencyTypeId;
    }

    public void setDependencyTypeId(int dependencyTypeId) {
        this.dependencyTypeId = dependencyTypeId;
    }
}
