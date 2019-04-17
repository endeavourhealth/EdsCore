package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCds;

import java.util.UUID;

public interface StagingCdsDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingCds cds) throws Exception;
    void save(StagingCds cds, UUID serviceId) throws Exception;
}
