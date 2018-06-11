package org.endeavourhealth.core.database.dal.publisherTransform;

import org.hl7.fhir.instance.model.Reference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ResourceIdTransformDalI {

    UUID findOrCreateThreadSafe(UUID serviceId, String resourceType, String sourceId) throws Exception;
    Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, List<Reference> sourceReferences) throws Exception;
    Map<Reference, Reference> findSourceReferencesFromEdsReferences(UUID serviceId, List<Reference> edsReferences) throws Exception;



}
