package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;

import java.util.Date;

public interface TppMultiLexToCtv3MapDalI {

    TppMultiLexToCtv3Map getMultiLexToCTV3Map(int multiLexProductId) throws Exception;

    void updateLookupTable(String s3FilePath, Date dataDate) throws Exception;
}
