package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;

import java.util.UUID;

public interface StagingProcedureDalI {

    void save(StagingProcedure stagingProcedure, UUID serviceId) throws Exception;
}
