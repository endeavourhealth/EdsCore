package org.endeavourhealth.core.database.dal.eds;

import org.endeavourhealth.core.database.dal.eds.models.PatientSearch;
import org.hl7.fhir.instance.model.EpisodeOfCare;
import org.hl7.fhir.instance.model.Patient;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PatientSearchDalI {

    void update(UUID serviceId, UUID systemId, Patient fhirPatient) throws Exception;
    void update(UUID serviceId, UUID systemId, EpisodeOfCare fhirEpisode) throws Exception;
    void deleteForService(UUID serviceId, UUID systemId) throws Exception;
    List<PatientSearch> searchByNhsNumber(String nhsNumber) throws Exception;
    List<PatientSearch> searchByLocalId(UUID serviceId, UUID systemId, String localId) throws Exception;
    List<PatientSearch> searchByLocalId(Set<String> serviceIds, String localId) throws Exception;
    List<PatientSearch> searchByDateOfBirth(UUID serviceId, UUID systemId, Date dateOfBirth) throws Exception;
    List<PatientSearch> searchByDateOfBirth(Set<String> serviceIds, Date dateOfBirth) throws Exception;
    List<PatientSearch> searchByNhsNumber(UUID serviceId, UUID systemId, String nhsNumber) throws Exception;
    List<PatientSearch> searchByNhsNumber(Set<String> serviceIds, String nhsNumber) throws Exception;
    List<PatientSearch> searchByNames(UUID serviceId, UUID systemId, List<String> names) throws Exception;
    List<PatientSearch> searchByNames(Set<String> serviceIds, List<String> names) throws Exception;
    PatientSearch searchByPatientId(UUID patientId) throws Exception;
    void deletePatient(UUID serviceId, UUID systemId, Patient fhirPatient) throws Exception;

}
