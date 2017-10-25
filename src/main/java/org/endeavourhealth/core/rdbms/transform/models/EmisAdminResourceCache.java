package org.endeavourhealth.core.rdbms.transform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "emis_admin_resource_cache", schema = "public", catalog = "transform")
public class EmisAdminResourceCache implements Serializable {

    private String dataSharingAgreementGuid = null;
    private String emisGuid = null;
    private String resourceType = null;
    private String resourceData = null;

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
