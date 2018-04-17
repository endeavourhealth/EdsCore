package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppImmunisationContent;

import java.util.Date;

public class TppImmunisationContent {

    private long rowId;
    private String name;
    private String content;
    private String serviceId;
    private Date dateDeleted;
    private ResourceFieldMappingAudit audit = null;

    public TppImmunisationContent(RdbmsTppImmunisationContent proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.name = proxy.getName();
        this.content = proxy.getContent();
        this.serviceId = proxy.getServiceId();
        this.dateDeleted = proxy.getDateDeleted();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppImmunisationContent(long rowId,
                         String name,
                         String content,
                         String serviceId,
                         Date dateDeleted,
                         ResourceFieldMappingAudit audit ) {
        this.rowId = rowId;
        this.name = name;
        this.content = content;
        this.serviceId = content;
        this.dateDeleted = dateDeleted;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
