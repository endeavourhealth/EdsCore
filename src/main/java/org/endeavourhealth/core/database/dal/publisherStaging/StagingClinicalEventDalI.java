package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingClinicalEvent;

import java.util.UUID;

public interface StagingClinicalEventDalI {

    void save(StagingClinicalEvent stagingClinicalEvent, UUID serviceId) throws Exception;
}
