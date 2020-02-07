package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.*;

import java.util.List;
import java.util.UUID;

public interface StagingTargetDalI {

    void processStagingForTargetCriticalCareCds(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingCriticalCareCdsTarget> getTargetCriticalCareCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetOutpatientCds(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingOutpatientCdsTarget> getTargetOutpatientCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetInpatientCds(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingInpatientCdsTarget> getTargetInpatientCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetEmergencyCds(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingEmergencyCdsTarget> getTargetEmergencyCds(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingProcedureTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetConditions(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingConditionTarget> getTargetConditions(UUID exchangeId, UUID serviceId) throws Exception;

    void processStagingForTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingClinicalEventTarget> getTargetClinicalEvents(UUID exchangeId, UUID serviceId) throws Exception;
}
