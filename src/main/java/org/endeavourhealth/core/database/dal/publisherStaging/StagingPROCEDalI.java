package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingPROCE;

import java.util.UUID;

public interface StagingPROCEDalI {

    void save(StagingPROCE stagingPROCE, UUID serviceId) throws Exception;
}
