package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherTransform.models.EmisCsvCodeMap;

import java.util.List;

public interface EmisTransformDalI {

    void save(EmisCsvCodeMap mapping) throws Exception;
    EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) throws Exception;

    void save(EmisAdminResourceCache resourceCache) throws Exception;
    void delete(EmisAdminResourceCache resourceCache) throws Exception;
    List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) throws Exception;
}
