package org.endeavourhealth.core.database.rdbms.publisherCommon.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "emis_admin_resource_cache_applied")
public class RdbmsEmisAdminResourceCacheApplied {

    private String serviceId;
    private String dataSharingAgreementGuid;
    private Date dateApplied;

    @Id
    @Column(name = "service_id", nullable = false)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "data_sharing_agreement_guid", nullable = false)
    public String getDataSharingAgreementGuid() {
        return dataSharingAgreementGuid;
    }

    public void setDataSharingAgreementGuid(String dataSharingAgreementGuid) {
        this.dataSharingAgreementGuid = dataSharingAgreementGuid;
    }

    @Column(name = "date_applied", nullable = false)
    public Date getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(Date dateApplied) {
        this.dateApplied = dateApplied;
    }
}
