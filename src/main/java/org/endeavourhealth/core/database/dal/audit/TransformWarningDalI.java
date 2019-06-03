package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.TransformWarning;

import java.util.List;
import java.util.UUID;

public interface TransformWarningDalI {

    void recordWarning(UUID serviceId, UUID systemId, UUID exchangeId, Integer publishedFileId, Integer recordNumber, String warningText, String... warningParams) throws Exception;
    void recordWarnings(List<TransformWarning> warnings) throws Exception;

}
