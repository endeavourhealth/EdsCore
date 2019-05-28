package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDiagnosis;

import java.util.UUID;

public interface StagingDiagnosisDalI {

    void save(StagingDiagnosis stagingDiagnosis, UUID serviceId) throws Exception;
}
