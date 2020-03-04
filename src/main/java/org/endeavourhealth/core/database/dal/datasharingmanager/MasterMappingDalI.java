package org.endeavourhealth.core.database.dal.datasharingmanager;

import java.util.List;

public interface MasterMappingDalI {

    public List<String> getParentMappings(String childUuid, Short childMapTypeId, Short parentMapTypeId) throws Exception;
    public List<String> getParentMappings(List<String> childUuids, Short childMapTypeId, Short parentMapTypeId) throws Exception;
    public List<String> getChildMappings(String parentUuid, Short parentMapTypeId, Short childMapTypeId) throws Exception;

}
