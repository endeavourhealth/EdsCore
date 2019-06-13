package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingPROCE;

import java.util.List;
import java.util.UUID;

public interface StagingPROCEDalI {

    void savePROCE(StagingPROCE stagingPROCE, UUID serviceId) throws Exception;
    void savePROCEs(List<StagingPROCE> stagingPROCEs, UUID serviceId) throws Exception;
}
