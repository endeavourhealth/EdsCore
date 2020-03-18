package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.MasterMappingEntity;

import java.util.List;

public interface MasterMappingDalI {

    public List<String> getParentMappings(String childUuid, Short childMapTypeId, Short parentMapTypeId) throws Exception;
    public List<String> getParentMappings(List<String> childUuids, Short childMapTypeId, Short parentMapTypeId) throws Exception;
    public List<String> getChildMappings(String parentUuid, Short parentMapTypeId, Short childMapTypeId) throws Exception;
    public List<MasterMappingEntity> getChildMappingsInLastXDays(Short parentMapTypeId, Short childMapTypeId, int days) throws Exception;

}
