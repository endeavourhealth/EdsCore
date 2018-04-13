package org.endeavourhealth.core.database.dal.reference;

import org.endeavourhealth.core.database.dal.reference.models.MultiLexToCTV3Map;

public interface MultiLexToCTV3MapDalI {

    MultiLexToCTV3Map getMultiLexToCTV3Map(long multiLexProductId) throws Exception;
}
