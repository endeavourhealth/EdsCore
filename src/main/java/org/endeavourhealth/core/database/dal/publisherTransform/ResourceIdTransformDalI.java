package org.endeavourhealth.core.database.dal.publisherTransform;

import org.hl7.fhir.instance.model.Reference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ResourceIdTransformDalI {

    UUID findOrCreateThreadSafe(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception;
    Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, UUID systemId, List<Reference> sourceReferences) throws Exception;
    Map<Reference, Reference> findSourceReferencesFromEdsReferences(List<Reference> edsReferences) throws Exception;

    //public void insert(ResourceIdMap resourceIdMap) throws Exception;
    //public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception;
    /*public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception;
    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) throws Exception;*/



}
