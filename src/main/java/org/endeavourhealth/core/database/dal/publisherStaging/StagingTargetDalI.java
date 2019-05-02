package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingTarget;

import java.util.List;
import java.util.UUID;

public interface StagingTargetDalI {

    void processStagingForTarget(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;
}
