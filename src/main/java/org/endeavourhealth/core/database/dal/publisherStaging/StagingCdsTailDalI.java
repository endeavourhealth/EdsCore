package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingCdsTail;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingConditionCdsTail;
import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedureCdsTail;

import java.util.List;
import java.util.UUID;

public interface StagingCdsTailDalI {

    void saveProcedureTail(StagingProcedureCdsTail cdsTail, UUID serviceId) throws Exception;
    void saveProcedureTails(List<StagingProcedureCdsTail> cdsTails, UUID serviceId) throws Exception;

    void saveConditionTail(StagingConditionCdsTail cdsConditionTail, UUID serviceId) throws Exception;
    void saveConditionTails(List<StagingConditionCdsTail> cdsConditionTails, UUID serviceId) throws Exception;

    void saveCdsTails(List<StagingCdsTail> cdsTails, UUID serviceId) throws Exception;
}
