package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCC;

import java.util.UUID;

public interface StagingSURCCDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingSURCC surcc) throws Exception;
    void save(StagingSURCC surcc, UUID serviceId) throws Exception;
}
