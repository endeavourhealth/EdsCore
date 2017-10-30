package org.endeavourhealth.core.database.dal.admin;

import java.util.UUID;

public interface PatientCohortDalI {

    void saveInCohort(UUID protocolId, UUID serviceId, String nhsNumber, boolean inCohort) throws Exception;
    boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) throws Exception;
}
