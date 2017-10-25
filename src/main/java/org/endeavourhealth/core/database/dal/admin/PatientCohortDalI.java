package org.endeavourhealth.core.database.dal.admin;

import java.util.UUID;

public interface PatientCohortDalI {

    public boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) throws Exception;
}
