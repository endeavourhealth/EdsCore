package org.endeavourhealth.core.database.rdbms.eds;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.*;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.eds.models.PatientSearch;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearch;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearchLocalIdentifier;
import org.endeavourhealth.core.fhirStorage.metadata.ReferenceHelper;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsPatientSearchDal implements PatientSearchDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPatientSearchDal.class);

    public void update(UUID serviceId, UUID systemId, Patient fhirPatient) throws Exception {
        update(serviceId, systemId, fhirPatient, null);
    }

    public void update(UUID serviceId, UUID systemId, EpisodeOfCare fhirEpisode) throws Exception {
        update(serviceId, systemId, null, fhirEpisode);
    }

    private void update(UUID serviceId, UUID systemId, Patient fhirPatient, EpisodeOfCare fhirEpisode) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            performUpdateInTransaction(serviceId, systemId, fhirPatient, fhirEpisode, entityManager);

        } catch (Exception ex) {
            //if we get an exception during the above, it's probably because another thread has inserted for our
            //patient at the same time (since we file patient and episode resources in parallel), so we should rollback and just try again
            entityManager.getTransaction().rollback();

            try {
                performUpdateInTransaction(serviceId, systemId, fhirPatient, fhirEpisode, entityManager);

            } catch (Exception ex2) {
                //if we get an exception the second time around, we should rollback and throw the FIRST exception
                entityManager.getTransaction().rollback();
                throw ex;
            }

        } finally {
            entityManager.close();
        }
    }

    private static void performUpdateInTransaction(UUID serviceId, UUID systemId, Patient fhirPatient, EpisodeOfCare fhirEpisode, EntityManager entityManager) throws Exception {

        entityManager.getTransaction().begin();

        RdbmsPatientSearch patientSearch = createOrUpdatePatientSearch(serviceId, systemId, fhirPatient, fhirEpisode, entityManager);
        entityManager.persist(patientSearch);

        //only if we have a patient resource do we need to update the local identifiers
        if (fhirPatient != null) {

            List<RdbmsPatientSearchLocalIdentifier> identifiersToSave = new ArrayList<>();
            List<RdbmsPatientSearchLocalIdentifier> identifiersToDelete = new ArrayList<>();

            createOrUpdateLocalIdentifiers(serviceId, systemId, fhirPatient, entityManager, identifiersToSave, identifiersToDelete);

            //do the deletes
            for (RdbmsPatientSearchLocalIdentifier localIdentifier: identifiersToDelete) {
                entityManager.remove(localIdentifier);
            }

            //do the saves
            for (RdbmsPatientSearchLocalIdentifier localIdentifier: identifiersToSave) {

                //adding try/catch to investigate a problem that has happened once but can't be replicated
                //entityManager.persist(localIdentifier);
                try {
                    entityManager.persist(localIdentifier);

                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    if (msg.indexOf("A different object with the same identifier value was already associated with the session") > -1) {

                        LOG.error("Failed to persist PatientSearchLocalIdentifier for service " + localIdentifier.getServiceId()
                                + " system " + localIdentifier.getSystemId()
                                + " patient " + localIdentifier.getPatientId()
                                + " ID system " + localIdentifier.getLocalIdSystem()
                                + " ID value " + localIdentifier.getLocalId()
                                + " date " + localIdentifier.getLastUpdated().getTime());

                        LOG.error("Entity being persisted is in entity cache = " + entityManager.contains(localIdentifier));
                    }
                    throw ex;
                }
            }
        }

        entityManager.getTransaction().commit();
    }

    private static void createOrUpdateLocalIdentifiers(UUID serviceId, UUID systemId, Patient fhirPatient,
                                                       EntityManager entityManager,
                                                       List<RdbmsPatientSearchLocalIdentifier> identifiersToSave,
                                                       List<RdbmsPatientSearchLocalIdentifier> identifiersToDelete) {

        String patientId = findPatientId(fhirPatient, null);

        String sql = "select c"
                + " from "
                + " RdbmsPatientSearchLocalIdentifier c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.patientId = :patient_id";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearchLocalIdentifier.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("patient_id", patientId);

        List<RdbmsPatientSearchLocalIdentifier> list = query.getResultList();

        if (fhirPatient.hasIdentifier()) {
            for (Identifier fhirIdentifier : fhirPatient.getIdentifier()) {

                if (!fhirIdentifier.getSystem().equalsIgnoreCase(FhirUri.IDENTIFIER_SYSTEM_NHSNUMBER)) {
                    String system = fhirIdentifier.getSystem();
                    String value = fhirIdentifier.getValue();

                    RdbmsPatientSearchLocalIdentifier localIdentifier = null;
                    for (RdbmsPatientSearchLocalIdentifier r: list) {
                        if (r.getLocalIdSystem().equals(system)
                            && r.getLocalId().equals(value)) {

                            localIdentifier = r;
                            break;
                        }
                    }

                    if (localIdentifier != null) {
                        //if the record already exists, remove it from the list so we know not to delete it
                        list.remove(localIdentifier);

                    } else {
                        //if there's no record for this local ID, create a new record
                        localIdentifier = new RdbmsPatientSearchLocalIdentifier();
                        localIdentifier.setServiceId(serviceId.toString());
                        localIdentifier.setSystemId(systemId.toString());
                        localIdentifier.setPatientId(patientId);
                        localIdentifier.setLocalIdSystem(system);
                        localIdentifier.setLocalId(value);
                    }

                    //always update the timestamp, so we know it's up to date
                    localIdentifier.setLastUpdated(new Date());

                    //add to the list to be saved
                    identifiersToSave.add(localIdentifier);
                }
            }
        }

        //any identifiers still in the list should now be deleted, since they're no longer in the patient
        for (RdbmsPatientSearchLocalIdentifier localIdentifier: list) {
            identifiersToDelete.add(localIdentifier);
        }

    }

    /*private static void performUpdateInTransaction(UUID serviceId, UUID systemId, Patient fhirPatient, EpisodeOfCare fhirEpisode, EntityManager entityManager) throws Exception {

        entityManager.getTransaction().begin();

        PatientSearch patientSearch = createOrUpdatePatientSearch(serviceId, systemId, fhirPatient, fhirEpisode, entityManager);
        entityManager.persist(patientSearch);

        //only if we have a patient resource do we need to update the local identifiers
        if (fhirPatient != null) {
            List<PatientSearchLocalIdentifier> localIdentifiers = createOrUpdateLocalIdentifiers(serviceId, systemId, fhirPatient, entityManager);
            for (PatientSearchLocalIdentifier localIdentifier: localIdentifiers) {

                //adding try/catch to investigate a problem that has happened once but can't be replicated
                //entityManager.persist(localIdentifier);
                try {
                    entityManager.persist(localIdentifier);

                    entityManager.

                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    if (msg.indexOf("A different object with the same identifier value was already associated with the session") > -1) {

                        LOG.error("Failed to persist PatientSearchLocalIdentifier for service " + localIdentifier.getServiceId()
                                + " system " + localIdentifier.getSystemId()
                                + " patient " + localIdentifier.getPatientId()
                                + " ID system " + localIdentifier.getLocalIdSystem()
                                + " ID value " + localIdentifier.getLocalId()
                                + " date " + localIdentifier.getLastUpdated().getTime());

                        LOG.error("Entity being persisted is in entity cache = " + entityManager.contains(localIdentifier));
                    }
                    throw ex;
                }
            }
        }

        entityManager.getTransaction().commit();
    }

    private static List<PatientSearchLocalIdentifier> createOrUpdateLocalIdentifiers(UUID serviceId, UUID systemId, Patient fhirPatient, EntityManager entityManager) {
        String patientId = findPatientId(fhirPatient, null);

        String sql = "select c"
                + " from "
                + " PatientSearchLocalIdentifier c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.patientId = :patient_id";

        Query query = entityManager.createQuery(sql, PatientSearchLocalIdentifier.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("patient_id", patientId);

        List<PatientSearchLocalIdentifier> list = query.getResultList();

        Map<String, PatientSearchLocalIdentifier> existingMap = new HashMap<>();
        for (PatientSearchLocalIdentifier localIdentifier: list) {
            String system = localIdentifier.getLocalIdSystem();
            existingMap.put(system, localIdentifier);
        }

        if (fhirPatient.hasIdentifier()) {
            for (Identifier fhirIdentifier : fhirPatient.getIdentifier()) {

                if (!fhirIdentifier.getSystem().equalsIgnoreCase(FhirUri.IDENTIFIER_SYSTEM_NHSNUMBER)) {
                    String system = fhirIdentifier.getSystem();
                    String value = fhirIdentifier.getValue();

                    PatientSearchLocalIdentifier localIdentifier = existingMap.get(system);
                    if (localIdentifier == null) {
                        localIdentifier = new PatientSearchLocalIdentifier();
                        localIdentifier.setServiceId(serviceId.toString());
                        localIdentifier.setSystemId(systemId.toString());
                        localIdentifier.setPatientId(patientId);
                        localIdentifier.setLocalIdSystem(system);

                        list.add(localIdentifier);
                    }

                    localIdentifier.setLocalId(value);
                    localIdentifier.setLastUpdated(new Date());
                }
            }
        }

        return list;
    }*/

    private static RdbmsPatientSearch createOrUpdatePatientSearch(UUID serviceId, UUID systemId, Patient fhirPatient, EpisodeOfCare fhirEpisode, EntityManager entityManager) throws Exception {
        String patientId = findPatientId(fhirPatient, fhirEpisode);

        String sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.serviceId = :serviceId"
                + " and c.systemId = :systemId"
                + " and c.patientId = :patientId";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("serviceId", serviceId.toString())
                .setParameter("systemId", systemId.toString())
                .setParameter("patientId", patientId);

        RdbmsPatientSearch patientSearch = null;
        try {
            patientSearch = (RdbmsPatientSearch)query.getSingleResult();

        } catch (NoResultException ex) {
            patientSearch = new RdbmsPatientSearch();
            patientSearch.setServiceId(serviceId.toString());
            patientSearch.setSystemId(systemId.toString());
            patientSearch.setPatientId(patientId);
        }

        if (fhirPatient != null) {

            String nhsNumber = IdentifierHelper.findNhsNumberTrueNhsNumber(fhirPatient);
            String forenames = findForenames(fhirPatient);
            String surname = findSurname(fhirPatient);
            String postcode = findPostcode(fhirPatient);
            String gender = findGender(fhirPatient);
            Date dob = fhirPatient.getBirthDate();
            Date dod = findDateOfDeath(fhirPatient);

            patientSearch.setNhsNumber(nhsNumber);
            patientSearch.setForenames(forenames);
            patientSearch.setSurname(surname);
            patientSearch.setPostcode(postcode);
            patientSearch.setGender(gender);
            patientSearch.setDateOfBirth(dob);
            patientSearch.setDateOfDeath(dod);
        }

        if (fhirEpisode != null) {

            Date regStart = null;
            Date regEnd = null;
            String orgTypeCode = null;

            if (fhirEpisode.hasPeriod()) {
                Period period = fhirEpisode.getPeriod();
                if (period.hasStart()) {
                    regStart = period.getStart();
                }
                if (period.hasEnd()) {
                    regEnd = period.getEnd();
                }
            }

            if (fhirEpisode.hasManagingOrganization()) {
                Reference orgReference = fhirEpisode.getManagingOrganization();
                ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(orgReference);
                ResourceType type = comps.getResourceType();
                String id = comps.getId();

                ResourceDalI resourceDalI = DalProvider.factoryResourceDal();
                Organization org = (Organization)resourceDalI.getCurrentVersionAsResource(type, id);
                if (org != null) {
                    CodeableConcept concept = org.getType();
                    orgTypeCode = CodeableConceptHelper.findCodingCode(concept, FhirValueSetUri.VALUE_SET_ORGANISATION_TYPE);
                }
            }

            patientSearch.setRegistrationStart(regStart);
            patientSearch.setRegistrationEnd(regEnd);
            patientSearch.setOrganisationTypeCode(orgTypeCode);
        }

        patientSearch.setLastUpdated(new Date());

        return patientSearch;
    }

    private static String findGender(Patient fhirPatient) {
        if (fhirPatient.hasGender()) {
            return fhirPatient.getGender().getDisplay();
        } else {
            return null;
        }
    }

    private static Date findDateOfDeath(Patient fhirPatient) throws Exception {
        if (fhirPatient.hasDeceasedDateTimeType()) {
            return fhirPatient.getDeceasedDateTimeType().getValue();
        } else {
            return null;
        }
    }


    private static String findForenames(Patient fhirPatient) {
        List<String> forenames = new ArrayList<>();

        for (HumanName fhirName: fhirPatient.getName()) {
            if (fhirName.getUse() != HumanName.NameUse.OFFICIAL) {
                continue;
            }

            for (StringType given: fhirName.getGiven()) {
                forenames.add(given.getValue());
            }
        }
        return String.join(" ", forenames);
    }

    private static String findSurname(Patient fhirPatient) {
        List<String> surnames = new ArrayList<>();

        for (HumanName fhirName: fhirPatient.getName()) {
            if (fhirName.getUse() != HumanName.NameUse.OFFICIAL) {
                continue;
            }

            for (StringType family: fhirName.getFamily()) {
                surnames.add(family.getValue());
            }
        }
        return String.join(" ", surnames);
    }

    private static String findPostcode(Patient fhirPatient) {

        for (Address fhirAddress: fhirPatient.getAddress()) {
            if (fhirAddress.getUse() != Address.AddressUse.HOME) {
                continue;
            }

            //Homerton seem to sometimes enter extra information in the postcode
            //field, making it longer than the 8 chars the field allows. So
            //simply truncate down
            String s = fhirAddress.getPostalCode();
            if (!Strings.isNullOrEmpty(s)
                    && s.length() > 8) {
                s = s.substring(0, 8);
            }
            return s;
            //return fhirAddress.getPostalCode();
        }
        return null;
    }

    private static String findPatientId(Patient fhirPatient, EpisodeOfCare fhirEpisode) {
        if (fhirPatient != null) {
            return fhirPatient.getId();

        } else {
            Reference reference = fhirEpisode.getPatient();
            return ReferenceHelper.getReferenceId(reference);
        }
    }



    public void deleteForService(UUID serviceId, UUID systemId) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        entityManager.getTransaction().begin();

        String sql = "delete"
                + " from"
                + " RdbmsPatientSearchLocalIdentifier c"
                + " where c.serviceId = :serviceId"
                + " and c.systemId = :systemId";

        Query query = entityManager.createQuery(sql)
                .setParameter("serviceId", serviceId.toString())
                .setParameter("systemId", systemId.toString());
        query.executeUpdate();

        sql = "delete"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.serviceId = :serviceId"
                + " and c.systemId = :systemId";

        query = entityManager.createQuery(sql)
                .setParameter("serviceId", serviceId.toString())
                .setParameter("systemId", systemId.toString());
        query.executeUpdate();

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public List<PatientSearch> searchByNhsNumber(String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.nhsNumber = :nhs_number";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("nhs_number", nhsNumber);

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByLocalId(UUID serviceId, UUID systemId, String localId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
            + " from"
            + " RdbmsPatientSearch c"
            + " inner join RdbmsPatientSearchLocalIdentifier l"
            + " on c.serviceId = l.serviceId"
            + " and c.systemId = l.systemId"
            + " and c.patientId = l.patientId"
            + " where l.localId = :localId"
            + " and l.serviceId = :serviceId"
            + " and l.systemId = :systemId";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
            .setParameter("localId", localId)
            .setParameter("serviceId", serviceId.toString())
            .setParameter("systemId", systemId.toString());

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByLocalId(Set<String> serviceIds, String localId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
            + " from"
            + " RdbmsPatientSearch c"
            + " inner join RdbmsPatientSearchLocalIdentifier l"
            + " on c.serviceId = l.serviceId"
            + " and c.systemId = l.systemId"
            + " and c.patientId = l.patientId"
            + " where l.localId = :localId"
            + " and l.serviceId IN :serviceIds";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
            .setParameter("localId", localId)
            .setParameter("serviceIds", serviceIds);

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByDateOfBirth(UUID serviceId, UUID systemId, Date dateOfBirth) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.dateOfBirth = :dateOfBirth"
                + " and c.serviceId = :serviceId"
                + " and c.systemId = :systemId";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("dateOfBirth", dateOfBirth)
                .setParameter("serviceId", serviceId.toString())
                .setParameter("systemId", systemId.toString());

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByDateOfBirth(Set<String> serviceIds, Date dateOfBirth) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
            + " from"
            + " RdbmsPatientSearch c"
            + " where c.dateOfBirth = :dateOfBirth"
            + " and c.serviceId IN :serviceIds";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
            .setParameter("dateOfBirth", dateOfBirth)
            .setParameter("serviceIds", serviceIds);

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByNhsNumber(UUID serviceId, UUID systemId, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.nhsNumber = :nhs_number"
                + " and c.serviceId = :serviceId"
                + " and c.systemId = :systemId";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("nhs_number", nhsNumber)
                .setParameter("serviceId", serviceId.toString())
                .setParameter("systemId", systemId.toString());

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByNhsNumber(Set<String> serviceIds, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
            + " from"
            + " RdbmsPatientSearch c"
            + " where c.nhsNumber = :nhs_number"
            + " and c.serviceId in :serviceIds";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
            .setParameter("nhs_number", nhsNumber)
            .setParameter("serviceIds", serviceIds);

        List<RdbmsPatientSearch> results = query.getResultList();
        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public List<PatientSearch> searchByNames(UUID serviceId, UUID systemId, List<String> names) throws Exception {

        if (names.isEmpty()) {
            throw new IllegalArgumentException("Names cannot be empty");
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        List<RdbmsPatientSearch> results = null;

        //if just one name, then treat as a surname
        if (names.size() == 1) {

            String surname = names.get(0) + "%";

            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where lower(c.surname) LIKE lower(:surname)"
                    + " and c.serviceId = :serviceId"
                    + " and c.systemId = :systemId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("surname", surname)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("systemId", systemId.toString());

            results = query.getResultList();

        } else {

            //if multiple tokens, then treat all but the last as forenames
            names = new ArrayList(names);
            String surname = names.remove(names.size()-1) + "%";
            String forenames = String.join("% ", names) + "%";

            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where lower(c.surname) LIKE lower(:surname)"
                    + " and lower(c.forenames) LIKE lower(:forenames)"
                    + " and c.serviceId = :serviceId"
                    + " and c.systemId = :systemId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("surname", surname)
                    .setParameter("forenames", forenames)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("systemId", systemId.toString());

            results = query.getResultList();
        }

        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }


    public List<PatientSearch> searchByNames(Set<String> serviceIds, List<String> names) throws Exception {

        if (names.isEmpty()) {
            throw new IllegalArgumentException("Names cannot be empty");
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        List<RdbmsPatientSearch> results = null;
        String name1;
        String name2;
        String sql;

        //if just one name, then treat as a surname
        if (names.size() == 1) {

					name1 = names.get(0).replace(",","") + "%";

            sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where (lower(c.surname) LIKE lower(:name1) or lower(c.forenames) LIKE lower(:name1))"
                + " and c.serviceId IN :serviceIds";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("name1", name1)
                .setParameter("serviceIds", serviceIds);

            results = query.getResultList();

        } else {

            //if multiple tokens, then treat all but the last as forenames
            names = new ArrayList(names);
					name1 = names.remove(names.size()-1).replace(",","") + "%";
					name2 = String.join("% ", names).replace(",","") + "%";

            sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where ("
									+ "(lower(c.surname) LIKE lower(:name2) and lower(c.forenames) LIKE lower(:name1))"
									+ " or "
									+ "(lower(c.surname) LIKE lower(:name1) and lower(c.forenames) LIKE lower(:name2))"
								+ ")"
                + " and c.serviceId IN :serviceIds";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("name1", name1)
                .setParameter("name2", name2)
                .setParameter("serviceIds", serviceIds);

            results = query.getResultList();
        }

        entityManager.close();
        return results
                .stream()
                .map(T -> new PatientSearch(T))
                .collect(Collectors.toList());
    }

    public PatientSearch searchByPatientId(UUID patientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsPatientSearch c"
                + " where c.patientId = :patientId";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                .setParameter("patientId", patientId.toString());

        try {
            RdbmsPatientSearch result = (RdbmsPatientSearch)query.getSingleResult();
            return new PatientSearch(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public void deletePatient(UUID serviceId, UUID systemId, Patient fhirPatient) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String patientId = findPatientId(fhirPatient, null);

            entityManager.getTransaction().begin();

            String sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchLocalIdentifier c"
                    + " where c.serviceId = :serviceId"
                    + " and c.systemId = :systemId"
                    + " and c.patientId = :patientId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("systemId", systemId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.serviceId = :serviceId"
                    + " and c.systemId = :systemId"
                    + " and c.patientId = :patientId";

            query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("systemId", systemId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }


}
