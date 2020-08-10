package org.endeavourhealth.core.database.dal.eds;

import org.endeavourhealth.core.database.dal.eds.models.PatientSearch;
import org.hl7.fhir.instance.model.EpisodeOfCare;
import org.hl7.fhir.instance.model.Patient;

import java.util.*;

public interface PatientSearchDalI {

    void update(UUID serviceId, Patient fhirPatient) throws Exception;
    void update(UUID serviceId, EpisodeOfCare fhirEpisode) throws Exception;
    void deleteForService(UUID serviceId) throws Exception;

    void deletePatient(UUID serviceId, Patient fhirPatient) throws Exception;
    void deleteEpisode(UUID serviceId, EpisodeOfCare episodeOfCare) throws Exception;

    List<PatientSearch> searchByLocalId(Set<String> serviceIds, String localId) throws Exception;
    List<PatientSearch> searchByDateOfBirth(Set<String> serviceIds, Date dateOfBirth) throws Exception;
    List<PatientSearch> searchByNhsNumber(Set<String> serviceIds, String nhsNumber) throws Exception;
    List<PatientSearch> searchByNames(Set<String> serviceIds, List<String> names) throws Exception;
    PatientSearch searchByPatientId(UUID patientId) throws Exception;

    Map<UUID, UUID> findPatientIdsForNhsNumber(Set<UUID> serviceIds, String nhsNumber) throws Exception;

    List<UUID> getPatientIds(UUID serviceId, boolean includeDeleted) throws Exception;
    List<UUID> getPatientIds(UUID serviceId, boolean includeDeleted, int max) throws Exception;
}
