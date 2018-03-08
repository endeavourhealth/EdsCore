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
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsPatientSearchDal implements PatientSearchDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPatientSearchDal.class);

    public void update(UUID serviceId, UUID systemId, Patient fhirPatient) throws Exception {

        String patientId = fhirPatient.getId();
        String nhsNumber = IdentifierHelper.findNhsNumberTrueNhsNumber(fhirPatient);
        String forenames = findForenames(fhirPatient);
        String surname = findSurname(fhirPatient);
        String postcode = findPostcode(fhirPatient);
        String gender = findGender(fhirPatient);
        Date dob = fhirPatient.getBirthDate();
        Date dod = findDateOfDeath(fhirPatient);

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            ps = createPatientPreparedStatement(entityManager);

            ps.setString(1, serviceId.toString());
            ps.setString(2, patientId);
            if (!Strings.isNullOrEmpty(nhsNumber)) {
                ps.setString(3, nhsNumber);
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(forenames)) {
                ps.setString(4, forenames);
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(surname)) {
                ps.setString(5, surname);
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            if (dob != null) {
                ps.setDate(6, new java.sql.Date(dob.getTime()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            if (dod != null) {
                ps.setDate(7, new java.sql.Date(dod.getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            if (!Strings.isNullOrEmpty(postcode)) {
                ps.setString(8, postcode);
            } else {
                ps.setNull(8, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(gender)) {
                ps.setString(9, gender);
            } else {
                ps.setNull(9, Types.VARCHAR);
            }
            ps.setDate(10, new java.sql.Date(new Date().getTime()));


            ps.executeUpdate();

            List<RdbmsPatientSearchLocalIdentifier> identifiersToSave = new ArrayList<>();
            List<RdbmsPatientSearchLocalIdentifier> identifiersToDelete = new ArrayList<>();

            createOrUpdateLocalIdentifiers(serviceId, fhirPatient, entityManager, identifiersToSave, identifiersToDelete);

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
                                + " patient " + localIdentifier.getPatientId()
                                + " ID system " + localIdentifier.getLocalIdSystem()
                                + " ID value " + localIdentifier.getLocalId()
                                + " date " + localIdentifier.getLastUpdated().getTime());

                        LOG.error("Entity being persisted is in entity cache = " + entityManager.contains(localIdentifier));
                    }
                    throw ex;
                }
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private String findRegistrationType(EpisodeOfCare fhirEpisode) {

        Extension extension = ExtensionConverter.findExtension(fhirEpisode, FhirExtensionUri.PATIENT_REGISTRATION_TYPE);
        if (extension != null) {
            Coding coding = (Coding)extension.getValue();
            return coding.getCode();
        }

        return null;
    }

    public void update(UUID serviceId, UUID systemId, EpisodeOfCare fhirEpisode) throws Exception {

        Reference reference = fhirEpisode.getPatient();
        String patientId = ReferenceHelper.getReferenceId(reference);

        Date regStart = null;
        Date regEnd = null;
        String orgTypeCode = null;
        String registrationType = findRegistrationType(fhirEpisode);

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
            Organization org = (Organization)resourceDalI.getCurrentVersionAsResource(serviceId, type, id);
            if (org != null) {
                CodeableConcept concept = org.getType();
                orgTypeCode = CodeableConceptHelper.findCodingCode(concept, FhirValueSetUri.VALUE_SET_ORGANISATION_TYPE);
            }
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            ps = createEpisodeOfCarePreparedStatement(entityManager);

            ps.setString(1, serviceId.toString());
            ps.setString(2, patientId);
            if (regStart != null) {
                ps.setDate(3, new java.sql.Date(regStart.getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            if (regEnd != null) {
                ps.setDate(4, new java.sql.Date(regEnd.getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            if (!Strings.isNullOrEmpty(orgTypeCode)) {
                ps.setString(5, orgTypeCode);
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.setDate(6, new java.sql.Date(new Date().getTime()));
            if (!Strings.isNullOrEmpty(registrationType)) {
                ps.setString(7, registrationType);
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    private static PreparedStatement createEpisodeOfCarePreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = null;

        //syntax for postreSQL is slightly different
        if (ConnectionManager.isPostgreSQL(connection)) {
            sql = "INSERT INTO patient_search"
                    + " (service_id, patient_id, registration_start, registration_end, organisation_type_code, last_updated, registration_type_code)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + " ON CONFLICT (service_id, system_id, patient_id) DO UPDATE SET"
                    + " registration_start = EXCLUDED.registration_start,"
                    + " registration_end = EXCLUDED.registration_end,"
                    + " organisation_type_code = EXCLUDED.organisation_type_code,"
                    + " last_updated = EXCLUDED.last_updated,"
                    + " registration_type_code = EXCLUDED.registration_type_code;";

        } else {
            sql = "INSERT INTO patient_search"
                    + " (service_id, patient_id, registration_start, registration_end, organisation_type_code, last_updated, registration_type_code)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " registration_start = VALUES(registration_start),"
                    + " registration_end = VALUES(registration_end),"
                    + " organisation_type_code = VALUES(organisation_type_code),"
                    + " last_updated = VALUES(last_updated),"
                    + " registration_type_code = VALUES(registration_type_code);";
        }

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createPatientPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        //syntax for postreSQL is slightly different
        String sql = null;
        if (ConnectionManager.isPostgreSQL(connection)) {
            sql = "INSERT INTO patient_search"
                    + " (service_id, patient_id, nhs_number, forenames, surname, date_of_birth, date_of_death, postcode, gender, last_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON CONFLICT (service_id, system_id, patient_id) DO UPDATE SET"
                    + " nhs_number = EXCLUDED.nhs_number,"
                    + " forenames = EXCLUDED.forenames,"
                    + " surname = EXCLUDED.surname,"
                    + " date_of_birth = EXCLUDED.date_of_birth,"
                    + " date_of_death = EXCLUDED.date_of_death,"
                    + " postcode = EXCLUDED.postcode,"
                    + " gender = EXCLUDED.gender,"
                    + " last_updated = EXCLUDED.last_updated;";

        } else {
            sql = "INSERT INTO patient_search"
                    + " (service_id, patient_id, nhs_number, forenames, surname, date_of_birth, date_of_death, postcode, gender, last_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " nhs_number = VALUES(nhs_number),"
                    + " forenames = VALUES(forenames),"
                    + " surname = VALUES(surname),"
                    + " date_of_birth = VALUES(date_of_birth),"
                    + " date_of_death = VALUES(date_of_death),"
                    + " postcode = VALUES(postcode),"
                    + " gender = VALUES(gender),"
                    + " last_updated = VALUES(last_updated);";
        }

        return connection.prepareStatement(sql);
    }

    private static void createOrUpdateLocalIdentifiers(UUID serviceId, Patient fhirPatient,
                                                       EntityManager entityManager,
                                                       List<RdbmsPatientSearchLocalIdentifier> identifiersToSave,
                                                       List<RdbmsPatientSearchLocalIdentifier> identifiersToDelete) {

        String patientId = findPatientId(fhirPatient, null);

        String sql = "select c"
                + " from "
                + " RdbmsPatientSearchLocalIdentifier c"
                + " where c.serviceId = :service_id"
                + " and c.patientId = :patient_id";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearchLocalIdentifier.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("patient_id", patientId);

        List<RdbmsPatientSearchLocalIdentifier> existingIdentifiers = query.getResultList();

        if (fhirPatient.hasIdentifier()) {
            for (Identifier fhirIdentifier : fhirPatient.getIdentifier()) {

                //NHS number is on the main patient_search table, so ignore any identifiers with that system here
                if (fhirIdentifier.getSystem().equalsIgnoreCase(FhirIdentifierUri.IDENTIFIER_SYSTEM_NHSNUMBER)) {
                    continue;
                }

                //if the identifier has been ended, skip it, since we don't want it on our search table
                if (fhirIdentifier.hasPeriod()) {
                    Period period = fhirIdentifier.getPeriod();
                    if (!PeriodHelper.isActive(period)) {
                        continue;
                    }
                }

                String system = fhirIdentifier.getSystem();
                String value = fhirIdentifier.getValue();

                RdbmsPatientSearchLocalIdentifier localIdentifier = null;
                for (RdbmsPatientSearchLocalIdentifier r: existingIdentifiers) {
                    if (r.getLocalIdSystem().equals(system)
                        && r.getLocalId().equals(value)) {

                        localIdentifier = r;
                        break;
                    }
                }

                if (localIdentifier != null) {
                    //if the record already exists, remove it from the list so we know not to delete it
                    existingIdentifiers.remove(localIdentifier);

                } else {
                    //if there's no record for this local ID, create a new record
                    localIdentifier = new RdbmsPatientSearchLocalIdentifier();
                    localIdentifier.setServiceId(serviceId.toString());
                    localIdentifier.setPatientId(patientId);
                    localIdentifier.setLocalIdSystem(system);
                    localIdentifier.setLocalId(value);
                }

                //always update the timestamp, so we know it's up to date
                localIdentifier.setLastUpdated(new Date());

                //we have some patients with multiple instances of the same Identifier, which causes Hibernate to
                //throw an error. So simply spot this and don't add to the list
                boolean alreadyAddedDuplicate = false;
                for (RdbmsPatientSearchLocalIdentifier identifierAlreadyToSave: identifiersToSave) {
                    if (identifierAlreadyToSave.getLocalIdSystem().equalsIgnoreCase(system)
                            && identifierAlreadyToSave.getLocalId().equalsIgnoreCase(value)) {
                        alreadyAddedDuplicate = true;
                        break;
                    }
                }
                if (alreadyAddedDuplicate) {
                    continue;
                }

                //add to the list to be saved
                identifiersToSave.add(localIdentifier);
            }
        }

        //any identifiers still in the list should now be deleted, since they're no longer in the patient
        for (RdbmsPatientSearchLocalIdentifier localIdentifier: existingIdentifiers) {
            identifiersToDelete.add(localIdentifier);
        }

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
        try {
            entityManager.getTransaction().begin();

            String sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchLocalIdentifier c"
                    + " where c.serviceId = :serviceId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString());
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.serviceId = :serviceId";

            query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString());
            query.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByNhsNumber(String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.nhsNumber = :nhs_number";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("nhs_number", nhsNumber);

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByLocalId(UUID serviceId, UUID systemId, String localId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " inner join RdbmsPatientSearchLocalIdentifier l"
                    + " on c.serviceId = l.serviceId"
                    + " and c.patientId = l.patientId"
                    + " where l.localId = :localId"
                    + " and l.serviceId = :serviceId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("localId", localId)
                    .setParameter("serviceId", serviceId.toString());

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByLocalId(Set<String> serviceIds, String localId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " inner join RdbmsPatientSearchLocalIdentifier l"
                    + " on c.serviceId = l.serviceId"
                    + " and c.patientId = l.patientId"
                    + " where l.localId = :localId"
                    + " and l.serviceId IN :serviceIds";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("localId", localId)
                    .setParameter("serviceIds", serviceIds);

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByDateOfBirth(UUID serviceId, UUID systemId, Date dateOfBirth) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.dateOfBirth = :dateOfBirth"
                    + " and c.serviceId = :serviceId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("dateOfBirth", dateOfBirth, TemporalType.DATE)
                    .setParameter("serviceId", serviceId.toString());

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByDateOfBirth(Set<String> serviceIds, Date dateOfBirth) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.dateOfBirth = :dateOfBirth"
                    + " and c.serviceId IN :serviceIds";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("dateOfBirth", dateOfBirth, TemporalType.DATE)
                    .setParameter("serviceIds", serviceIds);

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByNhsNumber(UUID serviceId, UUID systemId, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.nhsNumber = :nhs_number"
                    + " and c.serviceId = :serviceId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("nhs_number", nhsNumber)
                    .setParameter("serviceId", serviceId.toString());

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByNhsNumber(Set<String> serviceIds, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.nhsNumber = :nhs_number"
                    + " and c.serviceId in :serviceIds";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("nhs_number", nhsNumber)
                    .setParameter("serviceIds", serviceIds);

            List<RdbmsPatientSearch> results = query.getResultList();

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<PatientSearch> searchByNames(UUID serviceId, UUID systemId, List<String> names) throws Exception {

        if (names.isEmpty()) {
            throw new IllegalArgumentException("Names cannot be empty");
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            List<RdbmsPatientSearch> results = null;

            //if just one name, then treat as a surname
            if (names.size() == 1) {

                String surname = names.get(0) + "%";

                String sql = "select c"
                        + " from"
                        + " RdbmsPatientSearch c"
                        + " where lower(c.surname) LIKE lower(:surname)"
                        + " and c.serviceId = :serviceId";

                Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                        .setParameter("surname", surname)
                        .setParameter("serviceId", serviceId.toString());

                results = query.getResultList();

            } else {

                //if multiple tokens, then treat all but the last as forenames
                names = new ArrayList(names);
                String surname = names.remove(names.size() - 1) + "%";
                String forenames = String.join("% ", names) + "%";

                String sql = "select c"
                        + " from"
                        + " RdbmsPatientSearch c"
                        + " where lower(c.surname) LIKE lower(:surname)"
                        + " and lower(c.forenames) LIKE lower(:forenames)"
                        + " and c.serviceId = :serviceId";

                Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                        .setParameter("surname", surname)
                        .setParameter("forenames", forenames)
                        .setParameter("serviceId", serviceId.toString());

                results = query.getResultList();
            }

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }


    public List<PatientSearch> searchByNames(Set<String> serviceIds, List<String> names) throws Exception {

        if (names.isEmpty()) {
            throw new IllegalArgumentException("Names cannot be empty");
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            List<RdbmsPatientSearch> results = null;
            String name1;
            String name2;
            String sql;

            //if just one name, then treat as a surname
            if (names.size() == 1) {

                name1 = names.get(0).replace(",", "") + "%";

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
                name1 = names.remove(names.size() - 1).replace(",", "") + "%";
                name2 = String.join("% ", names).replace(",", "") + "%";

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

            return results
                    .stream()
                    .map(T -> new PatientSearch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public PatientSearch searchByPatientId(UUID patientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.patientId = :patientId";

            Query query = entityManager.createQuery(sql, RdbmsPatientSearch.class)
                    .setParameter("patientId", patientId.toString());

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
                    + " and c.patientId = :patientId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearch c"
                    + " where c.serviceId = :serviceId"
                    + " and c.patientId = :patientId";

            query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }


}
