package org.endeavourhealth.core.database.dal.publisherTransform;

import java.util.UUID;

public interface PatientMergeDalI {

    void recordMerge(UUID serviceId, UUID patientFrom, UUID patientTo);

}
