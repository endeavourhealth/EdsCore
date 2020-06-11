package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultilexProductToCtv3Map;

import java.util.Date;

public interface TppMultilexLookupDalI {

    TppMultilexProductToCtv3Map getMultilexToCtv3MapForProductId(int multiLexProductId) throws Exception;
    void updateProductIdToCtv3LookupTable(String s3FilePath, Date dataDate) throws Exception;

    String getMultilexActionGroupNameForId(int actionGroupId) throws Exception;
}
