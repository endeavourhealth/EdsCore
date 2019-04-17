package org.endeavourhealth.core.database.dal.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;
import org.hl7.fhir.instance.model.Enumerations;

import java.util.List;
import java.util.UUID;

public interface StagingProcedureDalI {

    boolean getRecordChecksumFiled(UUID serviceId, StagingProcedure stagingProcedure) throws Exception;
    void save(StagingProcedure stagingProcedure, UUID serviceId) throws Exception;
    List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception;
}
