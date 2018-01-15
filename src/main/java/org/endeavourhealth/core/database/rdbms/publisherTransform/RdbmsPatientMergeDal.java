package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.PatientMergeDalI;

import java.util.UUID;

public class RdbmsPatientMergeDal implements PatientMergeDalI {


    @Override
    public void recordMerge(UUID serviceId, UUID patientFrom, UUID patientTo) {
        //TODO
    }
}
