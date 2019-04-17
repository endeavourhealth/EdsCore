package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherStaging.models.StagingProcedure;
import org.hl7.fhir.instance.model.Enumerations;

import java.util.List;
import java.util.UUID;

public interface BartsStagingDataDalI {
    void saveBartsStagingDataProcedure(StagingProcedure stagingProcedure) throws Exception;
    List<UUID> getSusResourceMappings(UUID serviceId, String sourceRowId, Enumerations.ResourceType resourceType) throws Exception;
}
