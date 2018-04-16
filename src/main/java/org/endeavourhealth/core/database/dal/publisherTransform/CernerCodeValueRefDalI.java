package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;

import java.util.UUID;

public interface CernerCodeValueRefDalI {

    CernerCodeValueRef getCodeFromCodeSet(Long codeSet, String code, UUID serviceId) throws Exception;
    void save(CernerCodeValueRef mapping, UUID serviceId) throws Exception;

    CernerCodeValueRef getCodeWithoutCodeSet(String code, UUID serviceId) throws Exception;
    CernerCodeValueRef getCodeFromMultipleCodeSets(String code, UUID serviceId, Long... codeSets) throws Exception;
}
