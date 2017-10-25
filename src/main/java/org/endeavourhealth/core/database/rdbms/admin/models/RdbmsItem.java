package org.endeavourhealth.core.database.rdbms.admin.models;

import org.endeavourhealth.core.database.dal.admin.models.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "item", schema = "public")
public class RdbmsItem implements Serializable {

    private String id = null;
    private String auditId = null;
    private String xmlContent = null;
    private String title = null;
    private String description = null;
    private boolean isDeleted = false;

    public RdbmsItem() {}

    public RdbmsItem(Item proxy) {
        this.id = proxy.getId().toString();
        this.auditId = proxy.getAuditId().toString();
        this.xmlContent = proxy.getXmlContent();
        this.title = proxy.getTitle();
        this.description = proxy.getDescription();
        this.isDeleted = proxy.isDeleted();
    }

    @Id
    @Column(name = "id", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(name = "audit_id", nullable = false)
    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    @Column(name = "xml_content", nullable = true)
    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @Column(name = "title", nullable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "is_deleted", nullable = true)
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

}
