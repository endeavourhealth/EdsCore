package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.hl7.fhir.instance.model.ResourceType;

import java.util.UUID;

public interface SubscriberInstanceMappingDalI {

    //instance mapping
    UUID findInstanceMappedId(ResourceType resourceType, UUID resourceId) throws Exception;
    UUID findOrCreateInstanceMappedId(ResourceType resourceType, UUID resourceId, String mappingValue) throws Exception;
    void takeOverInstanceMapping(ResourceType resourceType, UUID oldMappedResourceId, UUID newMappedResourceId) throws Exception;

}
