package org.endeavourhealth.core.database.dal.audit;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface ServicePublisherAuditDalI {

    Boolean getLatestDpaState(UUID serviceId) throws Exception;
    void saveDpaState(UUID serviceId, boolean hasDpa) throws Exception;
    Map<Date, Boolean> getDpaHistory(UUID serviceId) throws Exception;
}
