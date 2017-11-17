package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExchangeBatchExtraResourceDalI {

    void saveExtraResource(UUID exchangeId, UUID batchId, ResourceType resourceType, UUID resourceId) throws Exception;
    Map<ResourceType, List<UUID>> findExtraResources(UUID exchangeId, UUID batchId) throws Exception;
}
