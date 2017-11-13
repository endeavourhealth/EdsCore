package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EnterpriseIdDalI {

    Long findOrCreateEnterpriseId(String resourceType, String resourceId) throws Exception;
    Long findEnterpriseId(String resourceType, String resourceId) throws Exception;
    void saveEnterpriseOrganisationId(String serviceId, String systemId, Long enterpriseId) throws Exception;
    Long findEnterpriseOrganisationId(String serviceId, String systemId) throws Exception;
    Long findOrCreateEnterprisePersonId(String discoveryPersonId) throws Exception;
    List<Long> findEnterprisePersonIdsForPersonId(String discoveryPersonId) throws Exception;
    void findEnterpriseIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;
    void findOrCreateEnterpriseIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;

    //instance mapping
    UUID findInstanceMappedId(ResourceType resourceType, UUID resourceId) throws Exception;
    UUID findOrCreateInstanceMappedId(ResourceType resourceType, UUID resourceId, String mappingValue) throws Exception;
    void takeOverInstanceMapping(ResourceType resourceType, UUID oldMappedResourceId, UUID newMappedResourceId) throws Exception;

}
