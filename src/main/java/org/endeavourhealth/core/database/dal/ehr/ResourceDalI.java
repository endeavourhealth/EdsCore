package org.endeavourhealth.core.database.dal.ehr;

import org.endeavourhealth.core.database.dal.ehr.models.Encounter;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ResourceDalI {

    void save(List<ResourceWrapper> wrappers) throws Exception;
    void delete(List<ResourceWrapper> wrappers) throws Exception;

    void save(ResourceWrapper resourceEntry) throws Exception;
    void delete(ResourceWrapper resourceEntry) throws Exception;
    void hardDeleteResourceAndAllHistory(ResourceWrapper resourceEntry) throws Exception;

    void saveEncounter(ResourceWrapper wrapper, Encounter encounter) throws Exception;

    Resource getCurrentVersionAsResource(UUID serviceId, ResourceType resourceType, String resourceIdStr) throws Exception;
    ResourceWrapper getCurrentVersion(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
    Map<String, ResourceWrapper> getCurrentVersionForReferences(UUID serviceId, List<String> references) throws Exception;

    List<ResourceWrapper> getResourceHistory(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
    List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId) throws Exception;
    List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId, String resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByPatientAllSystems(UUID serviceId, UUID patientId, String resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception;
    List<ResourceWrapper> getResourcesByServiceAllSystems(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception;
    List<ResourceWrapper> getResourcesForBatch(UUID serviceId, UUID batchId) throws Exception;
    List<ResourceWrapper> getCurrentVersionOfResourcesForBatch(UUID serviceId, UUID batchId) throws Exception;
    Long getResourceChecksum(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
    Map<String, Long> getResourceChecksumsForReferences(UUID serviceId, List<String> references) throws Exception;
    boolean dataExists(UUID serviceId) throws Exception;
    ResourceWrapper getFirstResourceByService(UUID serviceId, ResourceType resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType) throws Exception;
    <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, String resourceType, Class<T> classOfT) throws Exception;
    long getResourceCountByService(UUID serviceId, String resourceType) throws Exception;


}
