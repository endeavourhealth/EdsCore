package org.endeavourhealth.core.database.dal.subscriberTransform;

import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.dal.subscriberTransform.models.SubscriberId;

import java.util.List;
import java.util.Map;

public interface SubscriberResourceMappingDalI {

    //old-style functions for old compass/enterprise DB
    Long findEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception;
    void findEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;
    Long findOrCreateEnterpriseIdOldWay(String resourceType, String resourceId) throws Exception;
    void findOrCreateEnterpriseIdsOldWay(List<ResourceWrapper> resources, Map<ResourceWrapper, Long> ids) throws Exception;

    //new-style functions for new subscriber DB
    SubscriberId findSubscriberId(String resourceType, String resourceId) throws Exception;
    Map<ResourceWrapper, SubscriberId> findSubscriberIds(List<ResourceWrapper> resources) throws Exception;
    SubscriberId findOrCreateSubscriberId(String resourceType, String resourceId) throws Exception;
    Map<ResourceWrapper, SubscriberId> findOrCreateSubscriberIds(List<ResourceWrapper> resources) throws Exception;

    void updateDtUpdatedForSubscriber(Map<String, SubscriberId> hmResourceReferences) throws Exception;
}
