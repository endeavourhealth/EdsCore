package org.endeavourhealth.core.database.dal.subscriberTransform;

import java.util.List;

public interface SubscriberPersonMappingDalI {

    //person ID mapping
    Long findOrCreateEnterprisePersonId(String discoveryPersonId) throws Exception;
    List<Long> findEnterprisePersonIdsForPersonId(String discoveryPersonId) throws Exception;

}
