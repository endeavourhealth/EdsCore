package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;

import java.util.UUID;

public interface StagingCdsTailDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingCdsTail cdsTail) throws Exception;
    void save(StagingCdsTail cdsTail, UUID serviceId) throws Exception;
}
