package org.endeavourhealth.core.database.rdbms.eds;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.*;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.eds.models.PatientSearch;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.eds.models.RdbmsPatientSearchLocalIdentifier2;
import org.endeavourhealth.core.fhirStorage.metadata.ReferenceHelper;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class RdbmsPatientSearch2Dal implements PatientSearchDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPatientSearchDal.class);

    public void update(UUID serviceId, Patient fhirPatient) throws Exception {

        String patientId = fhirPatient.getId();
        String nhsNumber = IdentifierHelper.findNhsNumberTrueNhsNumber(fhirPatient);
        String forenames = findForenames(fhirPatient);
        String surname = findSurname(fhirPatient);
        String addressLine1 = findAddressLine(fhirPatient, 0);
        String addressLine2 = findAddressLine(fhirPatient, 1);
        String addressLine3 = findAddressLine(fhirPatient, 2);
        String city = findCity(fhirPatient);
        String district = findDistrict(fhirPatient);
        String postcode = findPostcode(fhirPatient);
        String gender = findGender(fhirPatient);
        Date dob = fhirPatient.getBirthDate();
        Date dod = findDateOfDeath(fhirPatient);
        String registeredPracticeOdsCode = findRegisteredPracticeOdsCode(serviceId, fhirPatient);

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            ps = createPatientPreparedStatement(entityManager);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            ps.setString(col++, patientId);
            if (!Strings.isNullOrEmpty(nhsNumber)) {
                ps.setString(col++, nhsNumber);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(forenames)) {
                ps.setString(col++, forenames);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(surname)) {
                ps.setString(col++, surname);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (dob != null) {
                ps.setDate(col++, new java.sql.Date(dob.getTime()));
            } else {
                ps.setNull(col++, Types.DATE);
            }
            if (dod != null) {
                ps.setDate(col++, new java.sql.Date(dod.getTime()));
            } else {
                ps.setNull(col++, Types.DATE);
            }
            if (!Strings.isNullOrEmpty(addressLine1)) {
                ps.setString(col++, addressLine1);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(addressLine2)) {
                ps.setString(col++, addressLine2);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(addressLine3)) {
                ps.setString(col++, addressLine3);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(city)) {
                ps.setString(col++, city);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(district)) {
                ps.setString(col++, district);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(postcode)) {
                ps.setString(col++, postcode);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(gender)) {
                ps.setString(col++, gender);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            ps.setDate(col++, new java.sql.Date(new Date().getTime()));
            if (!Strings.isNullOrEmpty(registeredPracticeOdsCode)) {
                ps.setString(col++, registeredPracticeOdsCode);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }

            ps.executeUpdate();

            List<RdbmsPatientSearchLocalIdentifier2> identifiersToSave = new ArrayList<>();
            List<RdbmsPatientSearchLocalIdentifier2> identifiersToDelete = new ArrayList<>();

            createOrUpdateLocalIdentifiers(serviceId, fhirPatient, entityManager, identifiersToSave, identifiersToDelete);

            //do the deletes
            for (RdbmsPatientSearchLocalIdentifier2 localIdentifier: identifiersToDelete) {
                entityManager.remove(localIdentifier);
            }

            //do the saves
            for (RdbmsPatientSearchLocalIdentifier2 localIdentifier: identifiersToSave) {

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

    private static String findRegisteredPracticeOdsCode(UUID serviceId, Patient fhirPatient) throws Exception {
        if (!fhirPatient.hasCareProvider()) {
            return null;
        }

        List<Reference> references = fhirPatient.getCareProvider();

        ResourceDalI resourceDalI = DalProvider.factoryResourceDal();

        //first check for an organisation reference
        for (Reference reference: references) {
            String orgOdsCode = findOrganisationOdsCodeFromOrganization(serviceId, reference);
            if (!Strings.isNullOrEmpty(orgOdsCode)) {
                return orgOdsCode;
            }
        }

        //if no org reference, follow a practitioner reference
        for (Reference reference: references) {
            String orgOdsCode = findOrganisationOdsCodeFromPractitioner(serviceId, reference);
            if (!Strings.isNullOrEmpty(orgOdsCode)) {
                return orgOdsCode;
            }
        }

        return null;
    }

    private static String findOrganisationOdsCodeFromPractitioner(UUID serviceId, Reference reference) throws Exception {

        ResourceDalI resourceDalI = DalProvider.factoryResourceDal();

        ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(reference);
        ResourceType type = comps.getResourceType();
        String id = comps.getId();
        if (type == ResourceType.Practitioner) {

            Practitioner practitioner = (Practitioner)resourceDalI.getCurrentVersionAsResource(serviceId, type, id);
            if (practitioner != null
                    && practitioner.hasPractitionerRole()) {

                //go by an active role first
                for (Practitioner.PractitionerPractitionerRoleComponent role: practitioner.getPractitionerRole()) {
                    if (!role.hasPeriod()
                            || PeriodHelper.isActive(role.getPeriod())) {

                        if (role.hasManagingOrganization()) {
                            Reference orgReference = role.getManagingOrganization();
                            String orgOdsCode = findOrganisationOdsCodeFromOrganization(serviceId, reference);
                            if (!Strings.isNullOrEmpty(orgOdsCode)) {
                                return orgOdsCode;
                            }
                        }
                    }
                }

                //failing finding an active role, check any role
                for (Practitioner.PractitionerPractitionerRoleComponent role: practitioner.getPractitionerRole()) {

                    if (role.hasManagingOrganization()) {
                        Reference orgReference = role.getManagingOrganization();
                        String orgOdsCode = findOrganisationOdsCodeFromOrganization(serviceId, orgReference);
                        if (!Strings.isNullOrEmpty(orgOdsCode)) {
                            return orgOdsCode;
                        }
                    }
                }
            }

        }

        return null;
    }

    /**
     * finds an organisation ODS code from a reference, returning null if it's not an Organization reference
     */
    private static String findOrganisationOdsCodeFromOrganization(UUID serviceId, Reference reference) throws Exception {

        ResourceDalI resourceDalI = DalProvider.factoryResourceDal();

        ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(reference);
        ResourceType type = comps.getResourceType();
        String id = comps.getId();
        if (type == ResourceType.Organization) {

            Organization org = (Organization)resourceDalI.getCurrentVersionAsResource(serviceId, type, id);
            if (org != null) {
                String odsCode = IdentifierHelper.findOdsCode(org);
                if (!Strings.isNullOrEmpty(odsCode)) {
                    return odsCode;
                }
            }

        }

        return null;
    }


    private static String findCity(Patient fhirPatient) {
        Address address = findAddress(fhirPatient);
        if (address != null
                && address.hasCity()) {
            return address.getCity();
        }

        return null;
    }

    private static String findDistrict(Patient fhirPatient) {
        Address address = findAddress(fhirPatient);
        if (address != null
                && address.hasDistrict()) {
            return address.getDistrict();
        }

        return null;
    }

    private static String findAddressLine(Patient fhirPatient, int index) {
        Address address = findAddress(fhirPatient);
        if (address != null
                && address.hasLine()) {
            List<StringType> lines = address.getLine();
            if (index < lines.size()) {
                StringType stringType = lines.get(index);
                return stringType.getValue();
            }
        }

        return null;
    }

    private String findRegistrationType(EpisodeOfCare fhirEpisode) {

        Extension extension = ExtensionConverter.findExtension(fhirEpisode, FhirExtensionUri.PATIENT_REGISTRATION_TYPE);
        if (extension != null) {
            Coding coding = (Coding)extension.getValue();
            return coding.getCode();
        }

        return null;
    }

    public void update(UUID serviceId, EpisodeOfCare fhirEpisode) throws Exception {

        Reference reference = fhirEpisode.getPatient();
        String patientId = ReferenceHelper.getReferenceId(reference);
        String episodeId = fhirEpisode.getId();

        Date regStart = null;
        Date regEnd = null;
        String careManager = null;
        String orgName = null;
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

        ResourceDalI resourceDalI = DalProvider.factoryResourceDal();

        if (fhirEpisode.hasCareManager()) {
            Reference practitionerReference = fhirEpisode.getCareManager();
            ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(practitionerReference);
            ResourceType type = comps.getResourceType();
            String id = comps.getId();

            Practitioner practitioner = (Practitioner)resourceDalI.getCurrentVersionAsResource(serviceId, type, id);
            if (practitioner != null
                    && practitioner.hasName()) {
                HumanName name = practitioner.getName();
                careManager = name.getText();
            }
        }

        if (fhirEpisode.hasManagingOrganization()) {
            Reference orgReference = fhirEpisode.getManagingOrganization();
            ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(orgReference);
            ResourceType type = comps.getResourceType();
            String id = comps.getId();

            Organization org = (Organization)resourceDalI.getCurrentVersionAsResource(serviceId, type, id);
            if (org != null) {
                orgName = org.getName();

                CodeableConcept concept = org.getType();
                orgTypeCode = CodeableConceptHelper.findCodingCode(concept, FhirValueSetUri.VALUE_SET_ORGANISATION_TYPE);
            }
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            ps = createEpisodeOfCarePreparedStatement(entityManager);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            ps.setString(col++, patientId);
            ps.setString(col++, episodeId);
            if (regStart != null) {
                ps.setDate(col++, new java.sql.Date(regStart.getTime()));
            } else {
                ps.setNull(col++, Types.DATE);
            }
            if (regEnd != null) {
                ps.setDate(col++, new java.sql.Date(regEnd.getTime()));
            } else {
                ps.setNull(col++, Types.DATE);
            }
            if (!Strings.isNullOrEmpty(careManager)) {
                ps.setString(col++, careManager);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(orgName)) {
                ps.setString(col++, orgName);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(orgTypeCode)) {
                ps.setString(col++, orgTypeCode);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(registrationType)) {
                ps.setString(col++, registrationType);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            ps.setDate(col++, new java.sql.Date(new Date().getTime()));

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

        String sql = "INSERT INTO patient_search_episode_2"
                + " (service_id, patient_id, episode_id, registration_start, registration_end, care_mananger, organisation_name, organisation_type_code, registration_type_code, last_updated)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " patient_id = VALUES(patient_id)," //even though this is part of the primary key, the patient ID may change due to merges, so update it here
                + " registration_start = VALUES(registration_start),"
                + " registration_end = VALUES(registration_end),"
                + " care_mananger = VALUES(care_mananger),"
                + " organisation_name = VALUES(organisation_name),"
                + " organisation_type_code = VALUES(organisation_type_code),"
                + " registration_type_code = VALUES(registration_type_code),"
                + " last_updated = VALUES(last_updated);";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createPatientPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO patient_search_2"
                + " (service_id, patient_id, nhs_number, forenames, surname, date_of_birth, date_of_death, address_line_1, address_line_2, address_line_3, city, district, postcode, gender, last_updated, registered_practice_ods_code)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " nhs_number = VALUES(nhs_number),"
                + " forenames = VALUES(forenames),"
                + " surname = VALUES(surname),"
                + " date_of_birth = VALUES(date_of_birth),"
                + " date_of_death = VALUES(date_of_death),"
                + " address_line_1 = VALUES(address_line_1),"
                + " address_line_2 = VALUES(address_line_2),"
                + " address_line_3 = VALUES(address_line_3),"
                + " city = VALUES(city),"
                + " district = VALUES(district),"
                + " postcode = VALUES(postcode),"
                + " gender = VALUES(gender),"
                + " last_updated = VALUES(last_updated),"
                + " registered_practice_ods_code = VALUES(registered_practice_ods_code);";

        return connection.prepareStatement(sql);
    }

    private static void createOrUpdateLocalIdentifiers(UUID serviceId, Patient fhirPatient,
                                                       EntityManager entityManager,
                                                       List<RdbmsPatientSearchLocalIdentifier2> identifiersToSave,
                                                       List<RdbmsPatientSearchLocalIdentifier2> identifiersToDelete) {

        String patientId = findPatientId(fhirPatient, null);

        String sql = "select c"
                + " from "
                + " RdbmsPatientSearchLocalIdentifier2 c"
                + " where c.serviceId = :service_id"
                + " and c.patientId = :patient_id";

        Query query = entityManager.createQuery(sql, RdbmsPatientSearchLocalIdentifier2.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("patient_id", patientId);

        List<RdbmsPatientSearchLocalIdentifier2> existingIdentifiers = query.getResultList();

        if (fhirPatient.hasIdentifier()) {
            for (Identifier fhirIdentifier : fhirPatient.getIdentifier()) {

                //NHS number is on the main patient_search table, so ignore any identifiers with that system here
                if (fhirIdentifier.getSystem().equalsIgnoreCase(FhirIdentifierUri.IDENTIFIER_SYSTEM_NHSNUMBER)) {
                    continue;
                }

                //if the identifier has been ended, skip it, since we don't want it on our search table
                //no, this is a bad idea
                /*if (fhirIdentifier.hasPeriod()) {
                    Period period = fhirIdentifier.getPeriod();
                    if (!PeriodHelper.isActive(period)) {
                        continue;
                    }
                }*/

                String system = fhirIdentifier.getSystem();
                String value = fhirIdentifier.getValue();

                RdbmsPatientSearchLocalIdentifier2 localIdentifier = null;
                for (RdbmsPatientSearchLocalIdentifier2 r: existingIdentifiers) {
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
                    localIdentifier = new RdbmsPatientSearchLocalIdentifier2();
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
                for (RdbmsPatientSearchLocalIdentifier2 identifierAlreadyToSave: identifiersToSave) {
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
        for (RdbmsPatientSearchLocalIdentifier2 localIdentifier: existingIdentifiers) {
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

    /**
     * returns the HumanName to use from a Patient resource
     */
    private static HumanName findName(Patient fhirPatient) {

        List<HumanName> officialNames = new ArrayList<>();
        if (fhirPatient.hasName()) {
            for (HumanName fhirName : fhirPatient.getName()) {
                if (fhirName.getUse() == HumanName.NameUse.OFFICIAL) {
                    officialNames.add(fhirName);
                }
            }
        }

        //return first non-ended one
        for (HumanName name: officialNames) {
            if (!name.hasPeriod()
                    || PeriodHelper.isActive(name.getPeriod())) {
                return name;
            }
        }

        //if no non-ended one, then return the last one, as it was added most recently
        if (!officialNames.isEmpty()) {
            int size = officialNames.size();
            return officialNames.get(size-1);
        }

        return null;
    }

    private static String findForenames(Patient fhirPatient) {

        List<String> forenames = new ArrayList<>();

        HumanName fhirName = findName(fhirPatient);
        if (fhirName != null) {
            for (StringType given: fhirName.getGiven()) {
                forenames.add(given.getValue());
            }
        }
        return String.join(" ", forenames);
    }

    private static String findSurname(Patient fhirPatient) {
        List<String> surnames = new ArrayList<>();

        HumanName fhirName = findName(fhirPatient);
        if (fhirName != null) {
            for (StringType family: fhirName.getFamily()) {
                surnames.add(family.getValue());
            }
        }
        return String.join(" ", surnames);
    }

    private static Address findAddress(Patient fhirPatient) {

        List<Address> homeAddresses = new ArrayList<>();

        for (Address fhirAddress: fhirPatient.getAddress()) {
            if (fhirAddress.getUse() == Address.AddressUse.HOME) {
                homeAddresses.add(fhirAddress);
            }
        }

        //return first non-ended one
        for (Address address: homeAddresses) {
            if (!address.hasPeriod()
                    || PeriodHelper.isActive(address.getPeriod())) {
                return address;
            }
        }

        //if no non-ended one, then return the last one, as it was added most recently
        if (!homeAddresses.isEmpty()) {
            int size = homeAddresses.size();
            return homeAddresses.get(size-1);
        }

        return null;
    }

    private static String findPostcode(Patient fhirPatient) {

        Address fhirAddress = findAddress(fhirPatient);
        if (fhirAddress != null) {

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



    public void deleteForService(UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        try {
            entityManager.getTransaction().begin();

            String sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchLocalIdentifier2 c"
                    + " where c.serviceId = :serviceId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString());
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchEpisode2 c"
                    + " where c.serviceId = :serviceId";

            query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString());
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearch2 c"
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



    public List<PatientSearch> searchByLocalId(Set<String> serviceIds, String localId) throws Exception {
        return search(serviceIds, null, null, null, localId, null);
    }

    public List<PatientSearch> searchByDateOfBirth(Set<String> serviceIds, Date dateOfBirth) throws Exception {
        return search(serviceIds, null, null, dateOfBirth, null, null);
    }

    public List<PatientSearch> searchByNhsNumber(Set<String> serviceIds, String nhsNumber) throws Exception {
        return search(serviceIds, nhsNumber, null, null, null, null);
    }

    public List<PatientSearch> searchByNames(Set<String> serviceIds, List<String> names) throws Exception {
        return search(serviceIds, null, names, null, null, null);
    }

    public PatientSearch searchByPatientId(UUID patientId) throws Exception {
        List<PatientSearch> list = search(null, null, null, null, null, patientId);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    private List<PatientSearch> search(Set<String> serviceIds, String nhsNumber, List<String> names, Date dateOfBirth, String localId, UUID patientId) throws Exception {

        String sql = "SELECT ps.service_id, ps.patient_id, ps.nhs_number, ps.forenames, ps.surname, ps.date_of_birth, ps.date_of_death, ps.address_line_1, ps.address_line_2, ps.address_line_3, ps.city, ps.district, ps.postcode, ps.gender, ps.registered_practice_ods_code, "
                + " pse.episode_id, pse.registration_start, pse.registration_end, pse.care_mananger, pse.organisation_name, pse.organisation_type_code, pse.registration_type_code"
                + " FROM patient_search_2 ps"
                + " INNER JOIN patient_search_episode_2 pse"
                + " ON ps.patient_id = pse.patient_id"
                + " AND ps.service_id = pse.service_id";

        if (!Strings.isNullOrEmpty(localId)) {
            sql += " INNER JOIN patient_search_local_identifier_2 psi"
                    + " ON psi.patient_id = ps.patient_id"
                    + " AND psi.service_id = ps.service_id";
        }

        if (serviceIds != null) {
            sql += " WHERE ps.service_id IN (";
            sql += String.join(",", Collections.nCopies(serviceIds.size(), "?"));
            sql += ")";
        }

        if (!Strings.isNullOrEmpty(nhsNumber)) {
            sql += " AND ps.nhs_number = ?";

        } else if (names != null) {
            if (names.size() == 1) {
                sql += " AND (ps.surname LIKE ? OR ps.forenames LIKE ?)";
            } else {
                sql += " AND ((ps.forenames LIKE ? AND ps.surname LIKE ?)";
                sql += " OR (ps.forenames LIKE ? AND ps.surname LIKE ?))";
            }

        } else if (dateOfBirth != null) {
            sql += " AND ps.date_of_birth = ?";

        } else if (!Strings.isNullOrEmpty(localId)) {
            sql += " AND psi.local_id = ?";

        } else if (patientId != null) {
            //nasty hack, but when searching by patient ID we don't add service IDs, so need to add the where clause here
            sql += " WHERE ps.patient_id = ?";

        } else {
            throw new IllegalArgumentException("Insufficient parameters passed in to search function");
        }

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();
            ps = connection.prepareStatement(sql);

            int index = 1;

            if (serviceIds != null) {
                for (String serviceId: serviceIds) {
                    ps.setString(index++, serviceId);
                }
            }

            if (!Strings.isNullOrEmpty(nhsNumber)) {
                ps.setString(index++, nhsNumber);

            } else if (names != null) {

                //if just one name, then treat as a surname
                if (names.size() == 1) {

                    String searchToken = names.get(0).replace(",", "") + "%";
                    ps.setString(index++, searchToken);
                    ps.setString(index++, searchToken);

                } else {

                    //if multiple tokens, then treat all but the last as forenames
                    names = new ArrayList(names);
                    String searchToken1 = names.remove(names.size() - 1).replace(",", "") + "%";
                    String searchToken2 = String.join("% ", names).replace(",", "") + "%";

                    ps.setString(index++, searchToken1);
                    ps.setString(index++, searchToken2);
                    ps.setString(index++, searchToken2);
                    ps.setString(index++, searchToken1);
                }

            } else if (dateOfBirth != null) {
                ps.setDate(index++, new java.sql.Date(dateOfBirth.getTime()));

            } else if (!Strings.isNullOrEmpty(localId)) {
                ps.setString(index++, localId);

            } else if (patientId != null) {
                ps.setString(index++, patientId.toString());

            } else {
                throw new IllegalArgumentException("Insufficient parameters passed in to search function");
            }

            ResultSet rs = ps.executeQuery();

            List<PatientSearch> ret = new ArrayList<>();

            while (rs.next()) {

                PatientSearch obj = new PatientSearch();

                int col = 1;
                obj.setServiceId(UUID.fromString(rs.getString(col++)));
                obj.setPatientId(UUID.fromString(rs.getString(col++)));
                obj.setNhsNumber(rs.getString(col++));
                obj.setForenames(rs.getString(col++));
                obj.setSurname(rs.getString(col++));
                obj.setDateOfBirth(rs.getDate(col++));
                obj.setDateOfDeath(rs.getDate(col++));
                obj.setAddressLine1(rs.getString(col++));
                obj.setAddressLine2(rs.getString(col++));
                obj.setAddressLine3(rs.getString(col++));
                obj.setCity(rs.getString(col++));
                obj.setDistrict(rs.getString(col++));
                obj.setPostcode(rs.getString(col++));
                obj.setGender(rs.getString(col++));
                obj.setRegisteredPracticeOdsCode(rs.getString(col++));
                obj.setEpisodeId(UUID.fromString(rs.getString(col++)));
                obj.setRegistrationStart(rs.getDate(col++));
                obj.setRegistrationEnd(rs.getDate(col++));
                obj.setCareManager(rs.getString(col++));
                obj.setOrganisationName(rs.getString(col++));
                obj.setOrganisationTypeCode(rs.getString(col++));
                obj.setRegistrationTypeCode(rs.getString(col++));

                ret.add(obj);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    public void deletePatient(UUID serviceId, Patient fhirPatient) throws Exception {

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();

        try {
            String patientId = findPatientId(fhirPatient, null);

            entityManager.getTransaction().begin();

            String sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchLocalIdentifier2 c"
                    + " where c.serviceId = :serviceId"
                    + " and c.patientId = :patientId";

            Query query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearchEpisode2 c"
                    + " where c.serviceId = :serviceId"
                    + " and c.patientId = :patientId";

            query = entityManager.createQuery(sql)
                    .setParameter("serviceId", serviceId.toString())
                    .setParameter("patientId", patientId);
            query.executeUpdate();

            sql = "delete"
                    + " from"
                    + " RdbmsPatientSearch2 c"
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
