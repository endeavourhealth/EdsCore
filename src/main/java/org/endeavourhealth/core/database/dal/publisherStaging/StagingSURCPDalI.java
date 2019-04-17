package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;

import java.util.UUID;

public interface StagingSURCPDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingSURCP surcp) throws Exception;
    void save(StagingSURCP surcp, UUID serviceId) throws Exception;
}
