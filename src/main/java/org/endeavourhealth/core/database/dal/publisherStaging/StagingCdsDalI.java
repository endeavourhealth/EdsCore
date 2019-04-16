package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;

import java.util.UUID;

public interface StagingCdsDalI {

    void save(StagingCds mapping, UUID serviceId) throws Exception;
}
