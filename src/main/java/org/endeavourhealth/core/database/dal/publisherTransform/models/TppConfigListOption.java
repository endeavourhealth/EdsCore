package org.endeavourhealth.core.database.dal.publisherTransform.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsTppConfigListOption;

public class TppConfigListOption {

    private long rowId;
    private long configListId;
    private String listOptionName;
    private String serviceId;
    private ResourceFieldMappingAudit audit = null;

    public TppConfigListOption() {}

    public TppConfigListOption(RdbmsTppConfigListOption proxy) throws Exception {

        this.rowId = proxy.getRowId();
        this.configListId = proxy.getConfigListId();
        this.listOptionName = proxy.getListOptionName();
        this.serviceId = proxy.getServiceId();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }
    public TppConfigListOption(long rowId,
                               long configListId,
                               String listOptionName,
                               String serviceId,
                               ResourceFieldMappingAudit audit) {
        this.rowId = rowId;
        this.configListId = configListId;
        this.listOptionName = listOptionName;
        this.serviceId = serviceId;
        this.audit = audit;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getConfigListId() { return configListId; }

    public void setConfigListId(long configListId) {
        this.configListId = configListId;
    }

    public String getListOptionName() {
        return listOptionName;
    }

    public void setListOptionName(String listOptionName) {
        this.listOptionName = listOptionName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
