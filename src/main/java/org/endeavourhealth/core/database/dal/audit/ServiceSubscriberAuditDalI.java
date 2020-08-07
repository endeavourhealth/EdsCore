package org.endeavourhealth.core.database.dal.audit;

import java.util.List;
import java.util.UUID;

public interface ServiceSubscriberAuditDalI {

    List<String> getLatestSubscribers(UUID serviceId) throws Exception;
    void saveSubscribers(UUID serviceId, List<String> subscriberConfigNames) throws Exception;
}
