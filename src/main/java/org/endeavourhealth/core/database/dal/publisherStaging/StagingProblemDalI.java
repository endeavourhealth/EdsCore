package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProblem;

import java.util.List;
import java.util.UUID;

public interface StagingProblemDalI {

    void saveProblem(StagingProblem stagingProblem, UUID serviceId) throws Exception;
    void saveProblems(List<StagingProblem> stagingProblems, UUID serviceId) throws Exception;
}
