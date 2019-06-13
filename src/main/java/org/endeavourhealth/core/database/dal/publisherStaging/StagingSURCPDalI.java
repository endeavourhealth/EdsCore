package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCP;

import java.util.List;
import java.util.UUID;

public interface StagingSURCPDalI {

    void saveSURCP(StagingSURCP surcp, UUID serviceId) throws Exception;
    void saveSURCPs(List<StagingSURCP> surcps, UUID serviceId) throws Exception;
}
