package org.endeavourhealth.core.rdbms.admin.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "item_dependency", schema = "public", catalog = "admin")
public class ItemDependency  implements Serializable {

    private String itemId = null;
    private String auditId = null;
    private String dependentItemId = null;
    private int dependencyTypeId = -1;

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
