package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMappingRef;

public interface TppMappingRefDalI {

    TppMappingRef getMappingFromRowId(Long rowId) throws Exception;
    TppMappingRef getMappingFromRowAndGroupId(Long rowId, Long groupId) throws Exception;

    void save(TppMappingRef mapping) throws Exception;
}
