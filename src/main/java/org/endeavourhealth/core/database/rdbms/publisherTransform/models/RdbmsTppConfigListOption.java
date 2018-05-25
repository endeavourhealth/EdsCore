package org.endeavourhealth.core.database.rdbms.publisherTransform.models;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppConfigListOption;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tpp_config_list_option")
public class RdbmsTppConfigListOption implements Serializable {
    private long rowId;
    private long configListId;
    private String listOptionName;
    private String serviceId;
    private String auditJson;

    public RdbmsTppConfigListOption() {}

    public RdbmsTppConfigListOption(TppConfigListOption proxy) throws Exception {
        this.rowId = proxy.getRowId();
        this.configListId = proxy.getConfigListId();
        this.listOptionName = proxy.getListOptionName();
        this.serviceId = proxy.getServiceId();
        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "row_id", nullable = false)
    public long getRowId() { return rowId; }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @Column(name = "config_list_id", nullable = false)
    public long getConfigListId() {
        return configListId;
    }

    public void setConfigListId(long configListId) {
        this.configListId = configListId;
    }

    @Column(name = "list_option_name", nullable = false)
    public String getListOptionName() {
        return listOptionName;
    }

    public void setListOptionName(String listOptionName) {
        this.listOptionName = listOptionName;
    }

    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdbmsTppConfigListOption that = (RdbmsTppConfigListOption) o;
        return rowId == that.rowId &&
                configListId == that.configListId &&
                Objects.equals(listOptionName, that.listOptionName) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(auditJson, that.auditJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, configListId, listOptionName, serviceId, auditJson);
    }
}
