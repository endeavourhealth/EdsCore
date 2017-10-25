package org.endeavourhealth.core.database.cassandra.transform.models;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.endeavourhealth.core.database.dal.transform.models.EmisAdminResourceCache;

@Table(keyspace = "transform", name = "emis_admin_resource_cache")
public class CassandraEmisAdminResourceCache {

    @PartitionKey(0)
    @Column(name = "data_sharing_agreement_guid")
    private String dataSharingAgreementGuid = null;
    @ClusteringColumn(0)
    @Column(name = "emis_guid")
    private String emisGuid = null;
    @ClusteringColumn(1)
    @Column(name = "resource_type")
    private String resourceType = null;
    @Column(name = "resource_data")
    private String resourceData = null;

    public CassandraEmisAdminResourceCache() {}

    public CassandraEmisAdminResourceCache(EmisAdminResourceCache proxy) {
        this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid();
        this.emisGuid = proxy.getEmisGuid();
        this.resourceType = proxy.getResourceType();
        this.resourceData = proxy.getResourceData();
    }

    public String getDataSharingAgreementGuid() {
        return dataSharingAgreementGuid;
    }

    public void setDataSharingAgreementGuid(String dataSharingAgreementGuid) {
        this.dataSharingAgreementGuid = dataSharingAgreementGuid;
    }

    public String getEmisGuid() {
        return emisGuid;
    }

    public void setEmisGuid(String emisGuid) {
        this.emisGuid = emisGuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData;
    }
}
