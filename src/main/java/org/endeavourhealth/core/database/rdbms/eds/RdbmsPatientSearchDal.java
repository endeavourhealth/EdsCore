package org.endeavourhealth.core.database.rdbms.eds;

import com.google.common.base.Strings;
import org.endeavourhealth.common.fhir.*;
import org.endeavourhealth.common.fhir.schema.NhsNumberVerificationStatus;
import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.eds.PatientSearchDalI;
import org.endeavourhealth.core.database.dal.eds.models.PatientSearch;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.fhirStorage.metadata.ReferenceHelper;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class RdbmsPatientSearchDal implements PatientSearchDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsPatientSearchDal.class);

    private static Set<String> cachedSearchableIdentifiers = null;

    public void update(UUID serviceId, Patient fhirPatient) throws Exception {
        int attempts = 5;
        while (true) {
            try {
                tryUpdate(serviceId, fhirPatient);
                return;

            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (attempts > 0
                        && msg != null
                        && msg.contains("timeout exceeded")) {
                    attempts --;
                    Thread.sleep(5000);
                    continue;
                }

                throw ex;
            }
        }
    }

    private void tryUpdate(UUID serviceId, Patient fhirPatient) throws Exception {

        Connection connection = ConnectionManager.getEdsConnection();

        Date now = new Date();

        try {

            createOrUpdatePatient(serviceId, fhirPatient, connection, now);
            createOrUpdateLocalIdentifiers(serviceId, fhirPatient, connection, now);
            //createOrUpdateAddresses(serviceId, fhirPatient, connection, now);

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            connection.close();
        }
    }

    /*private void createOrUpdateAddresses(UUID serviceId, Patient fhirPatient, Connection connection, Date now) throws Exception {

        String patientId = findPatientId(fhirPatient, null);

        PreparedStatement psUpsert = null;
        PreparedStatement psDelete = null;
        try {
            psUpsert = createUpsertAddressPreparedStatement(connection);
            psDelete = createDeleteAddressPreparedStatement(connection, false);

            int ordinal = 0;
            int col = 1;

            if (fhirPatient.hasAddress()) {
                for (Address address: fhirPatient.getAddress()) {

                    col = 1;

                    //TODO - populate upsert

                    ordinal ++;
                }
            }


            //delete any with a greater ordinal
            col = 1;
            psDelete.setString(col++, serviceId.toString());
            psDelete.setString(col++, patientId);
            psDelete.setInt(col++, ordinal);

            psDelete.executeUpdate();

        } finally {
            if (psUpsert != null) {
                psUpsert.close();
            }
            if (psDelete != null) {
                psDelete.close();
            }
        }
    }

    private PreparedStatement createUpsertAddressPreparedStatement(Connection connection) {
        String sql = "INSERT INTO patient_search_address"
                    + " (service_id, patient_id, ordinal, use, start_date, end_date, address_line_1, address_line_2,"
                    + " address_line_3, address_line_4, city, district, postcode, last_updated, uprn_results,"
                    + " uprn_last_updated)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " use = VALUES(use),"
                    + " start_date = VALUES(start_date),"
                    + " end_date = VALUES(end_date),"
                    + " address_line_1 = VALUES(address_line_1),"
                    + " address_line_2 = VALUES(address_line_2),"
                    + " address_line_3 = VALUES(address_line_3),"
                    + " address_line_4 = VALUES(address_line_4),"
                    + " city = VALUES(city),"
                    + " district = VALUES(district),"
                    + " postcode = VALUES(postcode),"
                    + " last_updated = VALUES(last_updated),"
                    + " uprn_results = VALUES(uprn_results),"
                    + " uprn_last_updated = VALUES(uprn_last_updated)";

//TODO move UPRN cols to separate table so we can maintain full history???

        return connection.prepareStatement(sql);
    }*/

    private PreparedStatement createDeleteAddressPreparedStatement(Connection connection, boolean deleteAllForPatient) throws Exception {
        String sql = null;
        if (deleteAllForPatient) {
            sql = "UPDATE patient_search_address"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND dt_deleted IS NULL";
        } else {
            sql = "UPDATE patient_search_address"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND ordinal >= ?";
        }

        return connection.prepareStatement(sql);
    }

    private void createOrUpdatePatient(UUID serviceId, Patient fhirPatient, Connection connection, Date now) throws Exception {
        String patientId = fhirPatient.getId();
        String nhsNumber = IdentifierHelper.findNhsNumber(fhirPatient);
        String forenames = NameHelper.findForenames(fhirPatient);
        String surname = NameHelper.findSurname(fhirPatient);
        String addressLine1 = AddressHelper.findAddressLine(fhirPatient, 0);
        String addressLine2 = AddressHelper.findAddressLine(fhirPatient, 1);
        String addressLine3 = AddressHelper.findAddressLine(fhirPatient, 2);
        String city = AddressHelper.findCity(fhirPatient);
        String district = AddressHelper.findDistrict(fhirPatient);
        String postcode = AddressHelper.findPostcode(fhirPatient);
        String gender = findGender(fhirPatient);
        Date dob = fhirPatient.getBirthDate();
        Date dod = findDateOfDeath(fhirPatient);
        String registeredPracticeOdsCode = findRegisteredPracticeOdsCode(serviceId, fhirPatient);
        OrganisationDetails managingOrg = findManagingOrganisationDetails(serviceId, fhirPatient);
        String orgOdsCode = managingOrg.getOdsCode();
        String orgName = managingOrg.getName();
        String orgTypeCode = managingOrg.getTypeCode();
        String nhsNumberVerificationCode = findNhsNumberVerificationCode(fhirPatient);
        boolean testPatient = findTestPatientFlag(fhirPatient);

        PreparedStatement psPatient = null;
        try {
            psPatient = createUpsertPatientPreparedStatement(connection);

            int col = 1;
            psPatient.setString(col++, serviceId.toString());
            psPatient.setString(col++, patientId);
            if (!Strings.isNullOrEmpty(nhsNumber)) {
                psPatient.setString(col++, nhsNumber);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(forenames)) {
                psPatient.setString(col++, forenames);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(surname)) {
                psPatient.setString(col++, surname);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (dob != null) {
                psPatient.setDate(col++, new java.sql.Date(dob.getTime()));
            } else {
                psPatient.setNull(col++, Types.DATE);
            }
            if (dod != null) {
                psPatient.setDate(col++, new java.sql.Date(dod.getTime()));
            } else {
                psPatient.setNull(col++, Types.DATE);
            }
            if (!Strings.isNullOrEmpty(addressLine1)) {
                psPatient.setString(col++, addressLine1);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(addressLine2)) {
                psPatient.setString(col++, addressLine2);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(addressLine3)) {
                psPatient.setString(col++, addressLine3);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(city)) {
                psPatient.setString(col++, city);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(district)) {
                psPatient.setString(col++, district);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(postcode)) {
                psPatient.setString(col++, postcode);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(gender)) {
                psPatient.setString(col++, gender);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            psPatient.setTimestamp(col++, new java.sql.Timestamp(now.getTime())); //last updated
            if (!Strings.isNullOrEmpty(registeredPracticeOdsCode)) {
                psPatient.setString(col++, registeredPracticeOdsCode);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            psPatient.setNull(col++, Types.TIMESTAMP);
            if (!Strings.isNullOrEmpty(orgOdsCode)) {
                psPatient.setString(col++, orgOdsCode);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(orgName)) {
                psPatient.setString(col++, orgName);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(orgTypeCode)) {
                psPatient.setString(col++, orgTypeCode);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            if (!Strings.isNullOrEmpty(nhsNumberVerificationCode)) {
                psPatient.setString(col++, nhsNumberVerificationCode);
            } else {
                psPatient.setNull(col++, Types.VARCHAR);
            }
            psPatient.setTimestamp(col++, new java.sql.Timestamp(now.getTime())); //dt_creation
            psPatient.setBoolean(col++, testPatient);

            psPatient.executeUpdate();

        } finally {
            if (psPatient != null) {
                psPatient.close();
            }
        }
    }

    private boolean findTestPatientFlag(Patient fhirPatient) {
        BooleanType isTestPatient = (BooleanType) ExtensionConverter.findExtensionValue(fhirPatient, FhirExtensionUri.PATIENT_IS_TEST_PATIENT);
        if (isTestPatient != null
                && isTestPatient.hasValue()) {
            return isTestPatient.getValue().booleanValue();
        } else {
            return false;
        }
    }

    private String findNhsNumberVerificationCode(Patient fhirPatient) {
        NhsNumberVerificationStatus s = IdentifierHelper.findNhsNumberVerificationStatus(fhirPatient);
        if (s != null) {
            return s.getCode();
        } else {
            return null;
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

    private String findRegistrationType(EpisodeOfCare fhirEpisode) {

        Extension extension = ExtensionConverter.findExtension(fhirEpisode, FhirExtensionUri.EPISODE_OF_CARE_REGISTRATION_TYPE);
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
        OrganisationDetails managingOrg = findManagingOrganisationDetails(serviceId, fhirEpisode);
        String orgOdsCode = managingOrg.getOdsCode();
        String orgName = managingOrg.getName();
        String orgTypeCode = managingOrg.getTypeCode();
        String registrationType = findRegistrationType(fhirEpisode);
        String registrationStatus = findRegistrationStatus(fhirEpisode);


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



        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();
            ps = createUpsertEpisodeOfCarePreparedStatement(entityManager);

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
            ps.setTimestamp(col++, new java.sql.Timestamp(new Date().getTime()));
            if (!Strings.isNullOrEmpty(registrationStatus)) {
                ps.setString(col++, registrationStatus);
            } else {
                ps.setNull(col++, Types.VARCHAR);
            }
            ps.setNull(col++, Types.TIMESTAMP);
            if (!Strings.isNullOrEmpty(orgOdsCode)) {
                ps.setString(col++, orgOdsCode);
            } else {
                ps.setNull(col++, Types.VARCHAR);
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

    private OrganisationDetails findManagingOrganisationDetails(UUID serviceId, Patient fhirPatient) throws Exception {
        if (!fhirPatient.hasManagingOrganization()) {
            return new OrganisationDetails(null, null, null);
        }

        return findOrganizationDetailsFromReference(serviceId, fhirPatient.getManagingOrganization());
    }

    private OrganisationDetails findManagingOrganisationDetails(UUID serviceId, EpisodeOfCare fhirEpisode) throws Exception {
        if (!fhirEpisode.hasManagingOrganization()) {
            return new OrganisationDetails(null, null, null);
        }

        return findOrganizationDetailsFromReference(serviceId, fhirEpisode.getManagingOrganization());
    }

    private OrganisationDetails findOrganizationDetailsFromReference(UUID serviceId, Reference orgReference) throws Exception {

        ReferenceComponents comps = org.endeavourhealth.common.fhir.ReferenceHelper.getReferenceComponents(orgReference);
        ResourceType type = comps.getResourceType();
        String id = comps.getId();

        ResourceDalI resourceDal = DalProvider.factoryResourceDal();
        Organization org = (Organization)resourceDal.getCurrentVersionAsResource(serviceId, type, id);
        if (org == null) {
            return new OrganisationDetails(null, null, null);
        }

        String orgName = org.getName();

        CodeableConcept concept = org.getType();
        String orgTypeCode = CodeableConceptHelper.findCodingCode(concept, FhirValueSetUri.VALUE_SET_ORGANISATION_TYPE);

        String orgOdsCode = IdentifierHelper.findOdsCode(org);

        return new OrganisationDetails(orgOdsCode, orgName, orgTypeCode);
    }

    private String findRegistrationStatus(EpisodeOfCare fhirEpisode) {

        //status is stored in a contained List resource, referred to by an extension
        if (!fhirEpisode.hasContained()) {
            return null;
        }

        Extension extension = ExtensionConverter.findExtension(fhirEpisode, FhirExtensionUri.EPISODE_OF_CARE_REGISTRATION_STATUS);
        if (extension == null) {
            return null;
        }
        Reference idReference = (Reference)extension.getValue();
        String idReferenceValue = idReference.getReference();
        idReferenceValue = idReferenceValue.substring(1); //remove the leading "#" char

        List_ list = null;
        for (Resource containedResource: fhirEpisode.getContained()) {
            if (containedResource.getId().equals(idReferenceValue)) {
                list = (List_)containedResource;
                break;
            }
        }
        if (list == null
                || !list.hasEntry()) {
            return null;
        }

        //status is on the most recent entry
        List<List_.ListEntryComponent> entries = list.getEntry();
        List_.ListEntryComponent entry = entries.get(entries.size()-1);
        if (!entry.hasFlag()) {
            return null;
        }

        CodeableConcept codeableConcept = entry.getFlag();
        return CodeableConceptHelper.findCodingCode(codeableConcept, FhirValueSetUri.VALUE_SET_REGISTRATION_STATUS);
    }

    private static PreparedStatement createUpsertLocalIdPreparedStatement(Connection connection) throws Exception {

        String sql = "INSERT INTO patient_search_local_identifier"
                + " (service_id, patient_id, local_id, local_id_system, last_updated, dt_deleted)"
                + " VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " service_id = VALUES(service_id)," //even though this is part of the primary key, the patient ID may change due to merges, so update it here
                + " patient_id = VALUES(patient_id),"
                + " local_id = VALUES(local_id),"
                + " local_id_system = VALUES(local_id_system),"
                + " last_updated = VALUES(last_updated),"
                + " dt_deleted = VALUES(dt_deleted)";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createUpsertEpisodeOfCarePreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO patient_search_episode"
                + " (service_id, patient_id, episode_id, registration_start, registration_end, care_mananger, organisation_name, organisation_type_code, registration_type_code, last_updated, registration_status_code, dt_deleted, ods_code)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " patient_id = VALUES(patient_id)," //even though this is part of the primary key, the patient ID may change due to merges, so update it here
                + " registration_start = VALUES(registration_start),"
                + " registration_end = VALUES(registration_end),"
                + " care_mananger = VALUES(care_mananger),"
                + " organisation_name = VALUES(organisation_name),"
                + " organisation_type_code = VALUES(organisation_type_code),"
                + " registration_type_code = VALUES(registration_type_code),"
                + " last_updated = VALUES(last_updated),"
                + " registration_status_code = VALUES(registration_status_code),"
                + " dt_deleted = VALUES(dt_deleted),"
                + " ods_code = VALUES(ods_code)";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createUpsertPatientPreparedStatement(Connection connection) throws Exception {

        String sql = "INSERT INTO patient_search"
                + " (service_id, patient_id, nhs_number, forenames, surname, date_of_birth, date_of_death, address_line_1, address_line_2,"
                + " address_line_3, city, district, postcode, gender, last_updated, registered_practice_ods_code, dt_deleted, ods_code,"
                + " organisation_name, organisation_type_code, nhs_number_verification_status, dt_created, test_patient)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
                + " registered_practice_ods_code = VALUES(registered_practice_ods_code),"
                + " dt_deleted = VALUES(dt_deleted),"
                + " ods_code = VALUES(ods_code),"
                + " organisation_name = VALUES(organisation_name),"
                + " organisation_type_code = VALUES(organisation_type_code),"
                + " nhs_number_verification_status = VALUES(nhs_number_verification_status),"
                //note - the "update" part does not update the dt_created field - this is purposefully left out
                + " test_patient = VALUES(test_patient)";

        return connection.prepareStatement(sql);
    }

    private static Set<String> getSearchableIdentifiers() {
        if (cachedSearchableIdentifiers == null) {
            Set<String> s = FhirIdentifierUri.getSearchablePatientIdentifiers();
            cachedSearchableIdentifiers = new HashSet<>(s);
        }
        return cachedSearchableIdentifiers;
    }

    private static void createOrUpdateLocalIdentifiers(UUID serviceId, Patient fhirPatient,
                                                       Connection connection,
                                                       Date now) throws Exception {


        String patientId = findPatientId(fhirPatient, null);
        String currentNhsNumber = IdentifierHelper.findNhsNumber(fhirPatient);

        Set<PatientSearchLocalIdentifier> existingIdentifiers = new HashSet<>();

        PreparedStatement ps = null;
        PreparedStatement psUpsertLocalId = null;
        PreparedStatement psDeleteLocalId = null;

        try {
            String sql = "select local_id, local_id_system"
                    + " from patient_search_local_identifier"
                    + " where service_id = ?"
                    + " and patient_id = ?"
                    + " and dt_deleted IS NULL";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            ps.setString(col++, patientId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PatientSearchLocalIdentifier id = new PatientSearchLocalIdentifier();
                id.setLocalId(rs.getString("local_id"));
                id.setLocalIdSystem(rs.getString("local_id_system"));

                existingIdentifiers.add(id);
            }

            psUpsertLocalId = createUpsertLocalIdPreparedStatement(connection);
            psDeleteLocalId = createDeleteLocalIdPreparedStatement(connection, false);


            Set<PatientSearchLocalIdentifier> identifiersToLeave = new HashSet<>();
            Set<PatientSearchLocalIdentifier> identifiersToSave = new HashSet<>();
            Set<PatientSearchLocalIdentifier> identifiersToDelete = new HashSet<>();


            if (fhirPatient.hasIdentifier()) {
                for (Identifier fhirIdentifier : fhirPatient.getIdentifier()) {

                    //NHS number is on the main patient_search table, so ignore any identifiers with that system here
                    String system = fhirIdentifier.getSystem();
                    if (!getSearchableIdentifiers().contains(system)) {
                        continue;
                    }

                    if (system.equals(FhirIdentifierUri.IDENTIFIER_SYSTEM_NHSNUMBER)) {
                        //the main table only includes the CURRENT NHS number, so allow this table to store any past ones (or just any others)
                        String value = fhirIdentifier.getValue();
                        if (value == null  //got some old patient records with no NHS number in the identifier
                                || value.equals(currentNhsNumber)) {
                            continue;
                        }
                    }

                    //if the identifier has been ended, skip it, since we don't want it on our search table
                    //no, this is a bad idea
                /*if (fhirIdentifier.hasPeriod()) {
                    Period period = fhirIdentifier.getPeriod();
                    if (!PeriodHelper.isActive(period)) {
                        continue;
                    }
                }*/

                    String value = fhirIdentifier.getValue();

                    PatientSearchLocalIdentifier id = new PatientSearchLocalIdentifier();
                    id.setLocalIdSystem(system);
                    id.setLocalId(value);

                    //if it's already on the DB, we don't need to do anything
                    if (existingIdentifiers.contains(id)) {
                        identifiersToLeave.add(id);

                    } else {
                        //we have some patients with multiple instances of the same Identifier, so add to
                        //this set too so we don't try to upsert the same one twice
                        existingIdentifiers.add(id);

                        identifiersToSave.add(id);
                    }
                }
            }

            //any identifiers still in the list should now be deleted, since they're no longer in the patient
            for (PatientSearchLocalIdentifier id: existingIdentifiers) {
                if (!identifiersToLeave.contains(id)
                        && !identifiersToSave.contains(id)) {

                    identifiersToDelete.add(id);
                }
            }

            //do the deletes
            for (PatientSearchLocalIdentifier id: identifiersToDelete) {

                col = 1;
                psDeleteLocalId.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
                psDeleteLocalId.setString(col++, serviceId.toString());
                psDeleteLocalId.setString(col++, patientId);
                psDeleteLocalId.setString(col++, id.getLocalId());
                psDeleteLocalId.setString(col++, id.getLocalIdSystem());
                psDeleteLocalId.executeUpdate();
            }

            for (PatientSearchLocalIdentifier id: identifiersToSave) {

                col = 1;
                psUpsertLocalId.setString(col++, serviceId.toString());
                psUpsertLocalId.setString(col++, patientId);
                psUpsertLocalId.setString(col++, id.getLocalId());
                psUpsertLocalId.setString(col++, id.getLocalIdSystem());
                psUpsertLocalId.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
                psUpsertLocalId.setNull(col++, Types.TIMESTAMP);
                psUpsertLocalId.executeUpdate();
            }

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (psUpsertLocalId != null) {
                psUpsertLocalId.close();
            }
            if (psDeleteLocalId != null) {
                psDeleteLocalId.close();
            }
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

        PreparedStatement psDeletePatient = null;
        PreparedStatement psDeleteEpisode = null;
        PreparedStatement psDeleteLocalId = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            entityManager.getTransaction().begin();

            String sql = "delete from patient_search_local_identifier"
                    + " where service_id = ?";
            psDeleteLocalId = connection.prepareStatement(sql);
            psDeleteLocalId.setString(1, serviceId.toString());
            psDeleteLocalId.executeUpdate();

            sql = "delete from patient_search_episode"
                    + " where service_id = ?";
            psDeleteEpisode = connection.prepareStatement(sql);
            psDeleteEpisode.setString(1, serviceId.toString());
            psDeleteEpisode.executeUpdate();

            sql = "delete from patient_search"
                    + " where service_id = ?";
            psDeletePatient = connection.prepareStatement(sql);
            psDeletePatient.setString(1, serviceId.toString());
            psDeletePatient.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psDeletePatient != null) {
                psDeletePatient.close();
            }
            if (psDeleteEpisode != null) {
                psDeleteEpisode.close();
            }
            if (psDeleteLocalId != null) {
                psDeleteLocalId.close();
            }
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
                + " FROM patient_search ps"
                + " LEFT OUTER JOIN patient_search_episode pse"
                + " ON ps.patient_id = pse.patient_id"
                + " AND ps.service_id = pse.service_id"
                + " AND pse.dt_deleted IS NULL";

        if (!Strings.isNullOrEmpty(localId)) {
            sql += " INNER JOIN patient_search_local_identifier psi"
                    + " ON psi.patient_id = ps.patient_id"
                    + " AND psi.service_id = ps.service_id"
                    + " AND psi.dt_deleted IS NULL";
        }

        //always exclude deleted ones
        sql += " WHERE ps.dt_deleted IS NULL";

        if (serviceIds != null) {
            sql += " AND ps.service_id IN (";
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
            sql += " AND ps.patient_id = ?";

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

            /*String sqlStr = ps.toString();
            long msStart = System.currentTimeMillis();*/

            ResultSet rs = ps.executeQuery();

            /*long msEnd = System.currentTimeMillis();
            LOG.debug("Searching for patient took " + (msEnd - msStart) + "ms: " + sqlStr);*/

            List<PatientSearch> ret = new ArrayList<>();

            while (rs.next()) {

                PatientSearch obj = new PatientSearch();

                int col = 1;

                //fields from patient_search
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

                //fields from patient search episode, which may be null
                String episodeId = rs.getString(col++);
                if (episodeId != null) {
                    obj.setEpisodeId(UUID.fromString(episodeId));
                    obj.setRegistrationStart(rs.getDate(col++));
                    obj.setRegistrationEnd(rs.getDate(col++));
                    obj.setCareManager(rs.getString(col++));
                    obj.setOrganisationName(rs.getString(col++));
                    obj.setOrganisationTypeCode(rs.getString(col++));
                    obj.setRegistrationTypeCode(rs.getString(col++));
                }

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

        Connection connection = ConnectionManager.getEdsConnection();

        PreparedStatement psDeletePatient = null;
        PreparedStatement psDeleteEpisode = null;
        PreparedStatement psDeleteLocalId = null;
        try {
            String patientId = findPatientId(fhirPatient, null);

            psDeletePatient = createDeletePatientPreparedStatement(connection);
            psDeleteEpisode = createDeleteEpisodePreparedStatement(connection, true);
            psDeleteLocalId = createDeleteLocalIdPreparedStatement(connection, true);
//TODO delete all addresses too

            Date d = new Date();

            int col = 1;
            psDeletePatient.setTimestamp(col++, new java.sql.Timestamp(d.getTime()));
            psDeletePatient.setString(col++, serviceId.toString());
            psDeletePatient.setString(col++, patientId);
            psDeletePatient.executeUpdate();

            col = 1;
            psDeleteEpisode.setTimestamp(col++, new java.sql.Timestamp(d.getTime()));
            psDeleteEpisode.setString(col++, serviceId.toString());
            psDeleteEpisode.setString(col++, patientId);
            psDeleteEpisode.executeUpdate();

            col = 1;
            psDeleteLocalId.setTimestamp(col++, new java.sql.Timestamp(d.getTime()));
            psDeleteLocalId.setString(col++, serviceId.toString());
            psDeleteLocalId.setString(col++, patientId);
            psDeleteLocalId.executeUpdate();

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psDeletePatient != null) {
                psDeletePatient.close();
            }
            if (psDeleteEpisode != null) {
                psDeleteEpisode.close();
            }
            if (psDeleteLocalId != null) {
                psDeleteLocalId.close();
            }
            connection.close();
        }
    }

    private static PreparedStatement createDeleteEpisodePreparedStatement(Connection connection, boolean deleteAllForPatient) throws Exception {

        String sql = null;

        if (deleteAllForPatient) {
            sql = "UPDATE patient_search_episode"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND dt_deleted IS NULL";

        } else {
            sql = "UPDATE patient_search_episode"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND episode_id = ?"
                    + " AND dt_deleted IS NULL";
        }

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createDeletePatientPreparedStatement(Connection connection) throws Exception {
        String sql = "UPDATE patient_search"
                + " SET dt_deleted = ?"
                + " WHERE service_id = ?"
                + " AND patient_id = ?"
                + " AND dt_deleted IS NULL";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createDeleteLocalIdPreparedStatement(Connection connection, boolean deleteAllForPatient) throws Exception {
        String sql = null;
        if (deleteAllForPatient) {
            sql = "UPDATE patient_search_local_identifier"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND dt_deleted IS NULL";
        } else {
            sql = "UPDATE patient_search_local_identifier"
                    + " SET dt_deleted = ?"
                    + " WHERE service_id = ?"
                    + " AND patient_id = ?"
                    + " AND local_id = ?"
                    + " AND local_id_system = ?"
                    + " AND dt_deleted IS NULL";
        }

        return connection.prepareStatement(sql);
    }

    @Override
    public void deleteEpisode(UUID serviceId, EpisodeOfCare episodeOfCare) throws Exception {

        Connection connection = ConnectionManager.getEdsConnection();

        PreparedStatement ps = null;
        try {
            String patientId = findPatientId(null, episodeOfCare);
            String episodeId = episodeOfCare.getId();

            ps = createDeleteEpisodePreparedStatement(connection, false);

            int col = 1;
            ps.setTimestamp(col++, new java.sql.Timestamp(new Date().getTime()));
            ps.setString(col++, serviceId.toString());
            ps.setString(col++, patientId);
            ps.setString(col++, episodeId);

            ps.executeUpdate();

            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    /**
     * faster way to look up patient (and service ID) for an NHS number, without the join to patient_search_episode
     * or the SQL filtering on a long list of service IDs
     */
    public Map<UUID, UUID> findPatientIdsForNhsNumber(Set<UUID> serviceIds, String nhsNumber) throws Exception {

        String sql = "SELECT ps.service_id, ps.patient_id"
                + " FROM patient_search ps"
                + " WHERE ps.nhs_number = ?";

        EntityManager entityManager = ConnectionManager.getEdsEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();
            ps = connection.prepareStatement(sql);

            ps.setString(1, nhsNumber);

            ResultSet rs = ps.executeQuery();

            Map<UUID, UUID> ret = new HashMap<>();

            while (rs.next()) {

                int col = 1;
                UUID serviceId = UUID.fromString(rs.getString(col++));
                UUID patientId = UUID.fromString(rs.getString(col++));

                if (serviceIds.contains(serviceId)) {
                    ret.put(patientId, serviceId);
                }
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<UUID> getPatientIds(UUID serviceId, boolean includeDeleted) throws Exception {
        return getPatientIdsImpl(serviceId, includeDeleted, null);
    }

    @Override
    public List<UUID> getPatientIds(UUID serviceId, boolean includeDeleted, int max) throws Exception {
        return getPatientIdsImpl(serviceId, includeDeleted, new Integer(max));
    }


    private List<UUID> getPatientIdsImpl(UUID serviceId, boolean includeDeleted, Integer maxRecords) throws Exception {

        Connection connection = ConnectionManager.getEdsConnection();
        PreparedStatement ps = null;
        try {

            String sql = "SELECT patient_id"
                    + " FROM patient_search"
                    + " WHERE service_id = ?";
            if (!includeDeleted) {
                sql += " AND dt_deleted IS NULL";
            }
            if (maxRecords != null) {
                sql += " LIMIT " + maxRecords;
            }

            ps = connection.prepareStatement(sql);
            ps.setFetchSize(1000); //no need to load full result set at once

            ps.setString(1, serviceId.toString());

            ResultSet rs = ps.executeQuery();

            List<UUID> ret = new ArrayList<>();

            while (rs.next()) {

                int col = 1;
                UUID patientId = UUID.fromString(rs.getString(col++));
                ret.add(patientId);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }

    }
}

class PatientSearchLocalIdentifier {

    private String localId = null;
    private String localIdSystem = null;

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getLocalIdSystem() {
        return localIdSystem;
    }

    public void setLocalIdSystem(String localIdSystem) {
        this.localIdSystem = localIdSystem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(localId, localIdSystem);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PatientSearchLocalIdentifier) {
            PatientSearchLocalIdentifier other = (PatientSearchLocalIdentifier)o;
            if (other.getLocalId().equalsIgnoreCase(getLocalId())
                    && other.getLocalIdSystem().equals(getLocalIdSystem())) {
                return true;
            }
        }
        return false;
    }
}

class OrganisationDetails {
    private String odsCode;
    private String name;
    private String typeCode;

    public OrganisationDetails(String odsCode, String name, String typeCode) {
        this.odsCode = odsCode;
        this.name = name;
        this.typeCode = typeCode;
    }

    public String getOdsCode() {
        return odsCode;
    }

    public String getName() {
        return name;
    }

    public String getTypeCode() {
        return typeCode;
    }
}
