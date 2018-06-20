package org.endeavourhealth.core.database.dal.eds;

import org.endeavourhealth.core.database.dal.eds.models.PatientLinkPair;
import org.hl7.fhir.instance.model.Patient;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PatientLinkDalI {

    PatientLinkPair updatePersonId(UUID serviceId, Patient fhirPatient) throws Exception;
    String getPersonId(String patientId) throws Exception;
    Map<String, String> getPatientAndServiceIdsForPerson(String personId) throws Exception;
    List<PatientLinkPair> getChangesSince(Date timestamp) throws Exception;
}
