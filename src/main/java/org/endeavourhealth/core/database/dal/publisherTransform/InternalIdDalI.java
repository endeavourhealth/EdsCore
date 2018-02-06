package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.InternalIdMap;
import java.util.UUID;

public interface InternalIdDalI {

    void upsertRecord(UUID serviceId, String idType, String sourceId, String destinationId) throws Exception;

    String getDestinationId(UUID serviceId, String idType, String sourceId) throws Exception;

}
