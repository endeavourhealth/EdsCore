package org.endeavourhealth.core.database.dal.ehr;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.UUID;

public interface ResourceDalI {

    void save(ResourceWrapper resourceEntry) throws Exception;
    void delete(ResourceWrapper resourceEntry) throws Exception;
    void hardDelete(ResourceWrapper keys) throws Exception;

    Resource getCurrentVersionAsResource(UUID serviceId, ResourceType resourceType, String resourceIdStr) throws Exception;
    ResourceWrapper getCurrentVersion(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
    List<ResourceWrapper> getResourceHistory(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
    List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId) throws Exception;
    List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId, String resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByPatientAllSystems(UUID serviceId, UUID patientId, String resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType, List<UUID> resourceIds) throws Exception;
    List<ResourceWrapper> getResourcesByServiceAllSystems(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception;
    List<ResourceWrapper> getResourcesForBatch(UUID serviceId, UUID batchId) throws Exception;
    Long getResourceChecksum(UUID serviceId, String resourceType, UUID resourceId, UUID patientId) throws Exception;
    boolean dataExists(UUID serviceId, UUID systemId) throws Exception;
    ResourceWrapper getFirstResourceByService(UUID serviceId, UUID systemId, ResourceType resourceType) throws Exception;
    List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType) throws Exception;
    <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, UUID systemId, String resourceType, Class<T> classOfT) throws Exception;
    long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) throws Exception;
}
