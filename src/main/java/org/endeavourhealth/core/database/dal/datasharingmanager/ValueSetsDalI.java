package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonValueSets;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ValueSetsCodesEntity;
import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ValueSetsEntity;

import java.util.ArrayList;
import java.util.List;

public interface ValueSetsDalI {

    public List<ValueSetsCodesEntity> getValueSetsCode(String valueSetsUUid) throws Exception;
    public void createValueSetsCodes(ArrayList<ValueSetsCodesEntity> codeSetCodes) throws Exception;
    public void deleteValueSetsCodes(String valueSetsUuid) throws Exception;

    public List<JsonValueSets> getAllValueSets(String expression, Integer pageNumber, Integer pageSize,
                                               String orderColumn, boolean descending) throws Exception;

    public Long getTotalNumber(String expression) throws Exception;
    public ValueSetsEntity getValuesSets(int id) throws Exception;
    public ValueSetsEntity deleteValuesSets(String uuid) throws Exception;
    public ValueSetsEntity createValuesSets(ValueSetsEntity valuesSets) throws Exception;
    public ValueSetsEntity updateValuesSets(ValueSetsEntity valuesSets) throws Exception;
    public JsonValueSets parseEntityToJson(ValueSetsEntity codeSet) throws Exception;

}
