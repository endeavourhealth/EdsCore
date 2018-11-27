package org.endeavourhealth.core.database.dal.audit;

import java.util.UUID;

public interface TransformWarningDalI {

    void recordWarning(UUID serviceId, UUID systemId, UUID exchangeId, Integer publishedFileId, Integer recordNumber, String warningText, String... warningParams) throws Exception;
}
