package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;

import java.util.UUID;

public interface StagingSURCPDalI {

    void save(StagingSURCP surcp, UUID serviceId) throws Exception;
}
