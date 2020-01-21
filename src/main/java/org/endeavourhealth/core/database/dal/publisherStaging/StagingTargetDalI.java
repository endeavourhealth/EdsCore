package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingClinicalEventTarget;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingConditionTarget;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedureTarget;

import java.util.List;
import java.util.UUID;

public interface StagingTargetDalI {

    void processStagingForTargetInpatientCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetEmergencyCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingProcedureTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;

    //TODO - calls in to methods from transform code
    void processStagingForTargetConditions(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingConditionTarget> getTargetConditions(UUID exchangeId, UUID serviceId) throws Exception;


    void processStagingForTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingClinicalEventTarget> getTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception;
}
