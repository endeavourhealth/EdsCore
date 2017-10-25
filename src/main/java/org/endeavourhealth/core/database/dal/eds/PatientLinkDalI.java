package org.endeavourhealth.core.database.dal.eds;

import org.endeavourhealth.core.database.dal.eds.models.PatientLinkPair;
import org.hl7.fhir.instance.model.Patient;

import java.util.Date;
import java.util.List;

public interface PatientLinkDalI {

    PatientLinkPair updatePersonId(Patient fhirPatient) throws Exception;
    String getPersonId(String patientId) throws Exception;
    List<String> getPatientIds(String personId) throws Exception;
    List<PatientLinkPair> getChangesSince(Date timestamp) throws Exception;
}
