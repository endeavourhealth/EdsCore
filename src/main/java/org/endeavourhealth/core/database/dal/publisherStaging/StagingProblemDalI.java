package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProblem;

import java.util.UUID;

public interface StagingProblemDalI {

    void save(StagingProblem stagingProblem, UUID serviceId) throws Exception;
}
