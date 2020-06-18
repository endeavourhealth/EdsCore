package org.endeavourhealth.core.database.dal.hl7receiver;

import org.endeavourhealth.core.database.dal.hl7receiver.models.ResourceId;

public interface Hl7ResourceIdDalI {

    ResourceId getResourceId(String scope, String resource, String uniqueId) throws Exception;
    void saveResourceId(ResourceId resourceId)  throws Exception;
    //void updateResourceId(ResourceId resourceId)  throws Exception;


}
