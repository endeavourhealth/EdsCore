package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExchangeBatchExtraResourceDalI {

    void saveExtraResource(UUID exchangeId, UUID batchId, String resourceType, UUID resourceId) throws Exception;
    Map<String, List<UUID>> findExtraResources(UUID exchangeId, UUID batchId) throws Exception;
}
