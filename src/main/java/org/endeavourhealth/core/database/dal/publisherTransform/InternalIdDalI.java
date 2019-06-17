package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.InternalIdMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface InternalIdDalI {

    void save(UUID serviceId, String idType, String sourceId, String destinationId) throws Exception;
    void save(List<InternalIdMap> mappings) throws Exception;

    String getDestinationId(UUID serviceId, String idType, String sourceId) throws Exception;
    Map<String, String> getDestinationIds(UUID serviceId, String idType, Set<String> sourceIds) throws Exception;
    List<InternalIdMap> getSourceId(UUID serviceId, String idType, String destinationId) throws Exception;

}
