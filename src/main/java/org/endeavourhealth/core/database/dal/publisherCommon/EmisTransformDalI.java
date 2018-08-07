package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCsvCodeMap;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.List;

public interface EmisTransformDalI {

    void saveCodeMappings(List<EmisCsvCodeMap> mappings) throws Exception;
    void saveCodeMapping(EmisCsvCodeMap mapping) throws Exception;
    EmisCsvCodeMap getCodeMapping(boolean medication, Long codeId) throws Exception;

    void saveAdminResource(EmisAdminResourceCache resourceCache) throws Exception;
    void deleteAdminResource(EmisAdminResourceCache resourceCache) throws Exception;
    void saveAdminResources(List<EmisAdminResourceCache> resourceCache) throws Exception;
    void deleteAdminResources(List<EmisAdminResourceCache> resourceCache) throws Exception;
    List<EmisAdminResourceCache> getAdminResources(String dataSharingAgreementGuid) throws Exception;
    EmisAdminResourceCache getAdminResource(String dataSharingAgreementGuid, ResourceType resourceType, String sourceId) throws Exception;
}
