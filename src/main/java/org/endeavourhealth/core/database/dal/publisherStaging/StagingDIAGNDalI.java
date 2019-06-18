package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDIAGN;

import java.util.List;
import java.util.UUID;

public interface StagingDIAGNDalI {

    void saveDIAGN(StagingDIAGN stagingDIAGN, UUID serviceId) throws Exception;
    void saveDIAGNs(List<StagingDIAGN> stagingDIAGNs, UUID serviceId) throws Exception;
}
