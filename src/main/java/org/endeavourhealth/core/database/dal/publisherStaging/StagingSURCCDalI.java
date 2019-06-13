package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingSURCC;

import java.util.List;
import java.util.UUID;

public interface StagingSURCCDalI {

    void saveSURCC(StagingSURCC surcc, UUID serviceId) throws Exception;
    void saveSURCCs(List<StagingSURCC> surccs, UUID serviceId) throws Exception;
}
