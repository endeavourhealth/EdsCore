package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;

import java.util.UUID;

public interface CernerCodeValueRefDalI {

    CernerCodeValueRef getCodeFromCodeSet(Long codeSet, Long code, UUID serviceId) throws Exception;
    void save(CernerCodeValueRef mapping) throws Exception;

}
