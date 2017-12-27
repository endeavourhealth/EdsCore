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

    public RdbmsEmisAdminResourceCache() {}

    public RdbmsEmisAdminResourceCache(EmisAdminResourceCache proxy) {
        this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid();
        this.emisGuid = proxy.getEmisGuid();
        this.resourceType = proxy.getResourceType();
        this.resourceData = proxy.getResourceData();
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
}
