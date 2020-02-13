package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.TppMultiLexToCtv3Map;

import java.util.List;

public interface TppMultiLexToCtv3MapDalI {

    TppMultiLexToCtv3Map getMultiLexToCTV3Map(long multiLexProductId) throws Exception;

    void save(TppMultiLexToCtv3Map mapping) throws Exception;
    void save(List<TppMultiLexToCtv3Map> mappings) throws Exception;
}
