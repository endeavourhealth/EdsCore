package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisAdminResourceCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emis_admin_resource_cache")
public class RdbmsEmisAdminResourceCache implements Serializable {

    private String dataSharingAgreementGuid = null;
    private String emisGuid = null;
    private String resourceType = null;
    private String resourceData = null;
    private String auditJson = null; //JSON giving the audit details of the resource, so they can be applied when saving to core DB

    public RdbmsEmisAdminResourceCache() {}

    public RdbmsEmisAdminResourceCache(EmisAdminResourceCache proxy) throws Exception {
        this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid();
        this.emisGuid = proxy.getEmisGuid();
        this.resourceType = proxy.getResourceType();
        this.resourceData = proxy.getResourceData();
        if (proxy.getAudit() != null) {
            this.auditJson = proxy.getAudit().writeToJson();
        }
    }

    @Id
    @Column(name = "data_sharing_agreement_guid", nullable = false)
    public String getDataSharingAgreementGuid() {
        return dataSharingAgreementGuid;
    }

    public void setDataSharingAgreementGuid(String dataSharingAgreementGuid) {
        this.dataSharingAgreementGuid = dataSharingAgreementGuid;
    }

    @Id
    @Column(name = "emis_guid", nullable = false)
    public String getEmisGuid() {
        return emisGuid;
    }

    public void setEmisGuid(String emisGuid) {
        this.emisGuid = emisGuid;
    }

    @Id
    @Column(name = "resource_type", nullable = false)
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Column(name = "resource_data", nullable = false)
    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }

    @Column(name = "audit_json", nullable = true)
    public String getAuditJson() {
        return auditJson;
    }

    public void setAuditJson(String auditJson) {
        this.auditJson = auditJson;
    }
}
