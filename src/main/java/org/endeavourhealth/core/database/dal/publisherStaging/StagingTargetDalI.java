package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingTarget;

import java.util.List;
import java.util.UUID;

public interface StagingTargetDalI {

    void processStagingForTarget(UUID exchangeId, UUID serviceId) throws Exception;
    List<RdbmsStagingTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;
}
