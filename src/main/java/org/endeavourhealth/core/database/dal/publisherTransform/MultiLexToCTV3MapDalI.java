package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.MultiLexToCTV3Map;

import java.util.UUID;

public interface MultiLexToCTV3MapDalI {

    MultiLexToCTV3Map getMultiLexToCTV3Map(long multiLexProductId, UUID serviceId) throws Exception;

    void save(MultiLexToCTV3Map mapping, UUID serviceId) throws Exception;
}
