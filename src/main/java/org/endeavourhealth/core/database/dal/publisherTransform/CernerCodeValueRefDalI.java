package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;

import java.util.UUID;

public interface CernerCodeValueRefDalI {

    CernerCodeValueRef getCodeFromCodeSet(Long codeSet, Long code, UUID serviceId) throws Exception;
    void save(CernerCodeValueRef mapping, UUID serviceId) throws Exception;

    CernerCodeValueRef getCodeWithoutCodeSet(Long code, UUID serviceId) throws Exception;
    CernerCodeValueRef getCodeFromMultipleCodeSets(Long code, UUID serviceId, Long... codeSets) throws Exception;
}
