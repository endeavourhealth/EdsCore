package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDIAGN;

import java.util.UUID;

public interface StagingDIAGNDalI {

    void save(StagingDIAGN stagingDIAGN, UUID serviceId) throws Exception;
}
