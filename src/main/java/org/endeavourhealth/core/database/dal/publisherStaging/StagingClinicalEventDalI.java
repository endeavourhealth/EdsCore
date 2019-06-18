package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingClinicalEvent;

import java.util.List;
import java.util.UUID;

public interface StagingClinicalEventDalI {

    void saveCLEVE(StagingClinicalEvent stagingClinicalEvent, UUID serviceId) throws Exception;
    void saveCLEVEs(List<StagingClinicalEvent> stagingClinicalEvent, UUID serviceId) throws Exception;
}
