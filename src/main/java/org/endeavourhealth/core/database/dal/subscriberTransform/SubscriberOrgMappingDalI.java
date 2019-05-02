package org.endeavourhealth.core.database.dal.subscriberTransform;

public interface SubscriberOrgMappingDalI {

    //org ID mapping
    void saveEnterpriseOrganisationId(String serviceId, Long enterpriseId) throws Exception;
    Long findEnterpriseOrganisationId(String serviceId) throws Exception;

}
