package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDiagnosis;

import java.util.List;
import java.util.UUID;

public interface StagingDiagnosisDalI {

    void saveDiag(StagingDiagnosis stagingDiag, UUID serviceId) throws Exception;
    void saveDiags(List<StagingDiagnosis> stagingDiags, UUID serviceId) throws Exception;
}
