package org.endeavourhealth.core.database.dal.admin.models;

import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsItem;

import java.util.UUID;

public class Item {

    private UUID id = null;
    private UUID auditId = null;
    private String xmlContent = null;
    private String title = null;
    private String description = null;
    private boolean isDeleted = false;

    public Item() {}

    /*public Item(CassandraItem proxy) {
        this.id = proxy.getId();
        this.auditId = proxy.getAuditId();
        this.xmlContent = proxy.getXmlContent();
        this.title = proxy.getTitle();
        this.description = proxy.getDescription();
        this.isDeleted = proxy.getIsDeleted();
    }*/

    public Item(RdbmsItem proxy) {
        this.id = UUID.fromString(proxy.getId());
        this.auditId = UUID.fromString(proxy.getAuditId());
        this.xmlContent = proxy.getXmlContent();
        this.title = proxy.getTitle();
        this.description = proxy.getDescription();
        this.isDeleted = proxy.isDeleted();
    }

    public static Item factoryNew(String title, Audit audit) {
        Item ret = new Item();
        ret.setAuditId(audit.getId());
        ret.setTitle(title);
        ret.setId(UUID.randomUUID());
        ret.setDeleted(false);
        return ret;
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

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
