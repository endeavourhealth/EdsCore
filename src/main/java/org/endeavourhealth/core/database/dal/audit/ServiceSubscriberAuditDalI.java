package org.endeavourhealth.core.database.dal.audit;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ServiceSubscriberAuditDalI {

    List<String> getLatestSubscribers(UUID serviceId) throws Exception;
    void saveSubscribers(UUID serviceId, List<String> subscriberConfigNames) throws Exception;
    Map<Date, List<String>> getSubscriberHistory(UUID serviceId) throws Exception;
}
