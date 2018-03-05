package org.endeavourhealth.core.database.dal.audit;

import java.util.UUID;

public interface TransformWarningDalI {

    void recordWarning(UUID serviceId, UUID systemId, UUID exchangeId, Long sourceFileRecordId, String warningText, String... warningParams) throws Exception;
}
