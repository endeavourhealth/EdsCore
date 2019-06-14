package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerClinicalEventMappingState;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerCodeValueRef;
import org.endeavourhealth.core.database.dal.publisherTransform.models.CernerNomenclatureRef;

import java.util.List;
import java.util.UUID;

public interface CernerCodeValueRefDalI {

    //CVREF
    CernerCodeValueRef getCodeFromCodeSet(Long codeSet, String code, UUID serviceId) throws Exception;
    void saveCVREF(CernerCodeValueRef mapping) throws Exception;
    void saveCVREFs(List<CernerCodeValueRef> mappings) throws Exception;

    CernerCodeValueRef getCodeWithoutCodeSet(String code, UUID serviceId) throws Exception;
    CernerCodeValueRef getCodeFromMultipleCodeSets(String code, UUID serviceId, Long... codeSets) throws Exception;
    List<CernerCodeValueRef> getCodesForCodeSet(UUID serviceId, Long codeSet) throws Exception;

    //NOMREF
    CernerNomenclatureRef getNomenclatureRefForId(UUID serviceId, Long nomenclatureId) throws Exception;
    CernerNomenclatureRef getNomenclatureRefForValueText(UUID serviceId, String valueText) throws Exception;
    void saveNOMREF(CernerNomenclatureRef nomenclatureRef) throws Exception;
    void saveNOMREFs(List<CernerNomenclatureRef> nomenclatureRefs) throws Exception;

    //CLEVE mapping
    void updateCleveMappingStateTable(CernerClinicalEventMappingState mapping) throws Exception;
    void deleteCleveMappingStateTable(CernerClinicalEventMappingState mapping) throws Exception;
    void updateCleveMappingStateTable(List<CernerClinicalEventMappingState> mappings) throws Exception;
    void deleteCleveMappingStateTable(List<CernerClinicalEventMappingState> mappings) throws Exception;
}
