package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;

import java.util.List;
import java.util.UUID;

public interface StagingProcedureDalI {

    void saveProcedure(StagingProcedure stagingProcedure, UUID serviceId) throws Exception;
    void saveProcedures(List<StagingProcedure> stagingProcedures, UUID serviceId) throws Exception;
}
