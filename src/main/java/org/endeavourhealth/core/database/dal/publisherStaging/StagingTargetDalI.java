package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingDiagnosisTarget;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedureTarget;

import java.util.List;
import java.util.UUID;

public interface StagingTargetDalI {

    void processStagingForTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingProcedureTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception;

    //TODO - calls in to methods from transform code
    void processStagingForTargetDiagnosis(UUID exchangeId, UUID serviceId) throws Exception;
    List<StagingDiagnosisTarget> getTargetDiagnosis(UUID exchangeId, UUID serviceId) throws Exception;
}
