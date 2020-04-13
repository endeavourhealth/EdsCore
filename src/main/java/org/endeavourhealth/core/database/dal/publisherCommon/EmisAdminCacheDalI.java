package org.endeavourhealth.core.database.dal.publisherCommon;

import java.util.UUID;

public interface EmisAdminCacheDalI {

    boolean wasAdminCacheApplied(UUID serviceId) throws Exception;
    void adminCacheWasApplied(UUID serviceId, String dataSharingAgreementGuid) throws Exception;




}
