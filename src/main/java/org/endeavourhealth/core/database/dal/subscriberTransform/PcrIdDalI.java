package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PcrIdDalI {

    Long findOrCreatePcrId(String resourceType, String resourceId) throws Exception;
    Long findPcrId(String resourceType, String resourceId) throws Exception;
    void savePcrOrganisationId(String serviceId, String systemId, Long pcrId) throws Exception;
    Long findPcrOrganisationId(String serviceId, String systemId) throws Exception;
    Long findOrCreatePcrPersonId(String discoveryPersonId) throws Exception;
    List<Long> findPcrPersonIdsForPersonId(String discoveryPersonId) throws Exception;
    void findPcrIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;
    void findOrCreatePcrIds(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;

    //instance mapping
    UUID findInstanceMappedId(ResourceType resourceType, UUID resourceId) throws Exception;
    UUID findOrCreateInstanceMappedId(ResourceType resourceType, UUID resourceId, String mappingValue) throws Exception;
    void takeOverInstanceMapping(ResourceType resourceType, UUID oldMappedResourceId, UUID newMappedResourceId) throws Exception;

}
