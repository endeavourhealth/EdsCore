package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.*;

import java.util.List;
import java.util.UUID;

public interface StagingCdsDalI {

    void saveProcedure(StagingProcedureCds cds, UUID serviceId) throws Exception;
    void saveProcedureCount(StagingProcedureCdsCount cds, UUID serviceId) throws Exception;
    void saveProcedures(List<StagingProcedureCds> cdses, UUID serviceId) throws Exception;
    void saveProcedureCounts(List<StagingProcedureCdsCount> cdses, UUID serviceId) throws Exception;

    void saveCondition(StagingConditionCds cds, UUID serviceId) throws Exception;
    void saveConditionCount(StagingConditionCdsCount cds, UUID serviceId) throws Exception;
    void saveConditions(List<StagingConditionCds> cdses, UUID serviceId) throws Exception;
    void saveConditionCounts(List<StagingConditionCdsCount> cdses, UUID serviceId) throws Exception;

    void saveCDSInpatients(List<StagingInpatientCds> cdses, UUID serviceId) throws Exception;
    void saveCDSOutpatients(List<StagingOutpatientCds> cdses, UUID serviceId) throws Exception;
    void saveCDSEmergencies(List<StagingEmergencyCds> cdses, UUID serviceId) throws Exception;
    void saveCDSCriticalCares(List<StagingCriticalCareCds> cdses, UUID serviceId) throws Exception;
    void saveCDSHomeDelBirths(List<StagingHomeDelBirthCds> cdses, UUID serviceId) throws Exception;
}
