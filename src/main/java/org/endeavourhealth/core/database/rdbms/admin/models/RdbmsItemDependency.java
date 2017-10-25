package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.core.database.dal.admin.models.ItemDependency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "item_dependency", schema = "public")
public class RdbmsItemDependency implements Serializable {

    private String itemId = null;
    private String auditId = null;
    private String dependentItemId = null;
    private int dependencyTypeId = -1;

    public RdbmsItemDependency() {}

    public RdbmsItemDependency(ItemDependency proxy) {
        this.itemId = proxy.getItemId().toString();
        this.auditId = proxy.getAuditId().toString();
        this.dependentItemId = proxy.getDependentItemId().toString();
        this.dependencyTypeId = proxy.getDependencyTypeId();
    }

    @Id
    @Column(name = "item_id", nullable = false)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Id
    @Column(name = "audit_id", nullable = false)
    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    @Id
    @Column(name = "dependent_item_id", nullable = false)
    public String getDependentItemId() {
        return dependentItemId;
    }

    public void setDependentItemId(String dependentItemId) {
        this.dependentItemId = dependentItemId;
    }

    @Id
    @Column(name = "dependency_type_id", nullable = false)
    public int getDependencyTypeId() {
        return dependencyTypeId;
    }

    public void setDependencyTypeId(int dependencyTypeId) {
        this.dependencyTypeId = dependencyTypeId;
    }

}
