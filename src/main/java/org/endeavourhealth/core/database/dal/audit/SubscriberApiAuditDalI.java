package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.SubscriberApiAudit;

public interface SubscriberApiAuditDalI {

    void saveSubscriberApiAudit(SubscriberApiAudit audit) throws Exception;
}
