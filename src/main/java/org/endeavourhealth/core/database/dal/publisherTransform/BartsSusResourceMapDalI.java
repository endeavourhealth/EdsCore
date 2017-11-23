package org.endeavourhealth.core.database.dal.publisherTransform;

import org.hl7.fhir.instance.model.Enumerations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BartsSusResourceMapDalI {
    void saveSusResourceMappings(UUID serviceId, String sourceRowId, Map<Enumerations.ResourceType, List<UUID>> resourceIds) throws Exception;
    void saveSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception;

    void deleteSusResourceMappings(UUID serviceId, String sourceRowId, Map<Enumerations.ResourceType, List<UUID>> resourceIds) throws Exception;
    void deleteSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType, List<UUID> resourceIds) throws Exception;

    Map<Enumerations.ResourceType, List<UUID>> getSusResourceMappings(UUID serviceId, String sourceRowId) throws Exception;
    List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception;
}
