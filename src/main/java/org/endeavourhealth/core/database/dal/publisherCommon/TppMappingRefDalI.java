package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMappingRef;

import java.util.Date;

public interface TppMappingRefDalI {

    TppMappingRef getMappingFromRowId(int rowId) throws Exception;

    void updateLookupTable(String s3FilePath, Date dataDate) throws Exception;
}
