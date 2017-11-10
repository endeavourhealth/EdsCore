package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.UUID;

public interface EnterpriseInstanceMapDalI {

    public UUID findInstanceMappedId(String resourceType, UUID resourceId) throws Exception;
    public UUID findOrCreateInstanceMappedId(String resourceType, UUID resourceId, String mappingValue) throws Exception;

    //public void

    //public void saveInstanceMapping(String resourceType, UUID resourceIdFrom, UUID resourceIdTo);
    //public String find
}
