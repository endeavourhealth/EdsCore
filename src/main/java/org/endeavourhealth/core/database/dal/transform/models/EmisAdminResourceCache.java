package org.endeavourhealth.core.database.dal.transform.models;

import org.endeavourhealth.core.database.cassandra.transform.models.CassandraEmisAdminResourceCache;
import org.endeavourhealth.core.database.rdbms.transform.models.RdbmsEmisAdminResourceCache;

public class EmisAdminResourceCache {

    private String dataSharingAgreementGuid = null;
    private String emisGuid = null;
    private String resourceType = null;
    private String resourceData = null;

    public EmisAdminResourceCache() {}

    public EmisAdminResourceCache(RdbmsEmisAdminResourceCache mySql) {
        this.dataSharingAgreementGuid = mySql.getDataSharingAgreementGuid();
        this.emisGuid = mySql.getEmisGuid();
        this.resourceType = mySql.getResourceType();
        this.resourceData = mySql.getResourceData();
    }

    public EmisAdminResourceCache(CassandraEmisAdminResourceCache mySql) {
        this.dataSharingAgreementGuid = mySql.getDataSharingAgreementGuid();
        this.emisGuid = mySql.getEmisGuid();
        this.resourceType = mySql.getResourceType();
        this.resourceData = mySql.getResourceData();
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
