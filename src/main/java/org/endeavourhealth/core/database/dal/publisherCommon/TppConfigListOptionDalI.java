package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppConfigListOption;

import java.util.Date;

public interface TppConfigListOptionDalI {

    TppConfigListOption getListOptionFromRowId(int rowId) throws Exception;

    void updateLookupTable(String s3FilePath, Date dataDate) throws Exception;
}
