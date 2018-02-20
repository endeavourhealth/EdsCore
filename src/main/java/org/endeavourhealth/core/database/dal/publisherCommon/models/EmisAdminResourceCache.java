package org.endeavourhealth.core.database.dal.publisherCommon.models;

import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisAdminResourceCache;

public class EmisAdminResourceCache {

    private String dataSharingAgreementGuid = null;
    private String emisGuid = null;
    private String resourceType = null;
    private String resourceData = null;
    private ResourceFieldMappingAudit audit = null;

    public EmisAdminResourceCache() {}

    public EmisAdminResourceCache(RdbmsEmisAdminResourceCache proxy) throws Exception {
        this.dataSharingAgreementGuid = proxy.getDataSharingAgreementGuid();
        this.emisGuid = proxy.getEmisGuid();
        this.resourceType = proxy.getResourceType();
        this.resourceData = proxy.getResourceData();
        if (!Strings.isNullOrEmpty(proxy.getAuditJson())) {
            this.audit = ResourceFieldMappingAudit.readFromJson(proxy.getAuditJson());
        }
    }

    /*public EmisAdminResourceCache(CassandraEmisAdminResourceCache mySql) {
        this.dataSharingAgreementGuid = mySql.getDataSharingAgreementGuid();
        this.emisGuid = mySql.getEmisGuid();
        this.resourceType = mySql.getResourceType();
        this.resourceData = mySql.getResourceData();
        //no audit JSON
    }*/

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

    public ResourceFieldMappingAudit getAudit() {
        return audit;
    }

    public void setAudit(ResourceFieldMappingAudit audit) {
        this.audit = audit;
    }
}
