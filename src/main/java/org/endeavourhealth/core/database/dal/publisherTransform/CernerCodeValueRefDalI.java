package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerNomenclatureRef;

import java.util.UUID;

public interface CernerCodeValueRefDalI {

    //CVREF
    CernerCodeValueRef getCodeFromCodeSet(Long codeSet, String code, UUID serviceId) throws Exception;
    void save(CernerCodeValueRef mapping, UUID serviceId) throws Exception;

    CernerCodeValueRef getCodeWithoutCodeSet(String code, UUID serviceId) throws Exception;
    CernerCodeValueRef getCodeFromMultipleCodeSets(String code, UUID serviceId, Long... codeSets) throws Exception;

    //NOMREF
    CernerNomenclatureRef getNomenclatureRefForId(UUID serviceId, Long nomenclatureId) throws Exception;
    void saveNomenclatureRef(CernerNomenclatureRef nomenclatureRef) throws Exception;
}
