package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingPROCE;

import java.util.UUID;

public interface StagingPROCEDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingPROCE stagingPROCE) throws Exception;
    void save(StagingPROCE stagingPROCE, UUID serviceId) throws Exception;
    //List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception;
}
