package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.TppMappingRef;

import java.util.UUID;

public interface TppMappingRefDalI {

    TppMappingRef getMappingFromRowId(Long rowId, UUID serviceId) throws Exception;
    TppMappingRef getMappingFromRowAndGroupId(Long rowId, Long groupId, UUID serviceId) throws Exception;

    void save(TppMappingRef mapping, UUID serviceId) throws Exception;
}
