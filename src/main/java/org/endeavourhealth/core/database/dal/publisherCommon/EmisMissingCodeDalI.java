package org.endeavourhealth.core.database.dal.publisherCommon;

import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCodeType;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisMissingCodes;

import java.util.Set;
import java.util.UUID;

public interface EmisMissingCodeDalI {

    //Emis missing code
    void saveMissingCodeError(EmisMissingCodes emisMissingCodesVals) throws Exception;
    Set<Long> retrieveMissingCodes(EmisCodeType emisCodeType, UUID serviceId) throws Exception;
    Set<String> retrievePatientGuidsForMissingCodes(Set<Long> emisMissingCodes, UUID serviceId) throws Exception;
    void setMissingCodesFixed(Set<Long> emisMissingCodes, UUID serviceId) throws Exception;
    UUID retrieveOldestExchangeIdForMissingCodes(Set<Long> emisMissingCodes, UUID serviceId) throws Exception;
}
