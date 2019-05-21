package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsCount;

import java.util.UUID;

public interface StagingCdsDalI {

    void save(StagingCds cds, UUID serviceId) throws Exception;
    void save(StagingCdsCount cds, UUID serviceId) throws Exception;
}
