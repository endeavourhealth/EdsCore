package org.endeavourhealth.core.database.rdbms.ehr;

import org.endeavourhealth.core.database.dal.ehr.CoreFilerDalI;
import org.endeavourhealth.core.database.dal.ehr.models.*;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.*;

public class RdbmsCoreFilerDal implements CoreFilerDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsCoreFilerDal.class);

    private static final String DUPLICATE_KEY_ERR = "Duplicate entry .* for key 'PRIMARY'";

    @Override
    public void save(UUID serviceId, List<CoreFilerWrapper> wrappers) throws Exception {
        //allow several attempts if it fails due to a deadlock
        DeadlockHandler h = new DeadlockHandler();

        while (true) {
            try {
                trySave(wrappers);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private UUID findServiceId(List<CoreFilerWrapper> wrappers) throws Exception {

        if (wrappers == null || wrappers.isEmpty()) {
            throw new IllegalArgumentException("trying to save null or empty resources");
        }

        UUID serviceId = null;
        for (CoreFilerWrapper wrapper : wrappers) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save resources for different services");
            }
        }
        return serviceId;
    }

    private void trySave(List<CoreFilerWrapper> wrappers) throws Exception {

        UUID serviceId = findServiceId(wrappers);
        Connection connection = ConnectionManager.getEhrConnection(serviceId);
        PreparedStatement ps = null;

        try {
            //get wrapper data type to derive statement type and pass object data
            for (CoreFilerWrapper wrapper : wrappers) {

                if (wrapper.isDeleted()) {

                    //TODO: deletion statement (hard delete?)
                } else {
                    ps = createAndPopulateUpsertPreparedStatement(connection, wrapper);
                }
                ps.execute();
            }

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

    @Override
    public void delete(UUID serviceId, List<CoreFilerWrapper> wrappers) throws Exception {
        for (CoreFilerWrapper wrapper : wrappers) {
            wrapper.setDeleted(true);
        }
        save(serviceId, wrappers);
    }

    @Override
    public void save(UUID serviceId, CoreFilerWrapper wrapper) throws Exception {
        List<CoreFilerWrapper> l = new ArrayList<>();
        l.add(wrapper);
        save(serviceId, l);
    }

    @Override
    public CoreId findOrCreateCoreId(UUID serviceId, byte coreTable, String sourceId) throws Exception {

        List<String> l = new ArrayList<>();
        l.add(sourceId);

        Map<String, CoreId> ids = findOrCreateCoreIds(serviceId, coreTable, l);
        return ids.get(sourceId);
    }

    @Override
    public Map<String, CoreId> findOrCreateCoreIds(UUID serviceId, byte coreTable, List<String> sourceIds) throws Exception {
        DeadlockHandler h = new DeadlockHandler();
        h.addOtherErrorMessageToHandler(DUPLICATE_KEY_ERR); //due to multi-threading, we may get duplicate key errors, so just try again
        while (true) {
            try {
                return tryFindOrCreateSubscriberIds(serviceId, coreTable, sourceIds);

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    private Map<String, CoreId> tryFindOrCreateSubscriberIds(UUID serviceId, byte coreTable, List<String> sourceIds) throws Exception {

        Map<String, CoreId> ret = new HashMap<>();

        Connection connection = ConnectionManager.getEhrConnection(serviceId);

        PreparedStatement psInsert = null;

        try {
            //look for any inserted already
            findSubscriberIdsImpl(serviceId, coreTable, sourceIds, ret, connection);

            //see which IDs weren't found
            List<String> sourceIdsRemaining = new ArrayList<>();
            for (String sourceId : sourceIds) {
                if (!ret.containsKey(sourceId)) {
                    sourceIdsRemaining.add(sourceId);
                }
            }

            //now generate new IDs for any ones not found
            if (!sourceIdsRemaining.isEmpty()) {

                String sql = "INSERT INTO core_id_map (service_id, core_table, source_id) "
                        + "VALUES (?, ?, ?)";

                psInsert = connection.prepareStatement(sql);

                for (String sourceId : sourceIdsRemaining) {
                    int col = 1;
                    psInsert.setString(col++, serviceId.toString());
                    psInsert.setInt(col++, coreTable);
                    psInsert.setString(col++, sourceId);

                    psInsert.addBatch();
                }

                psInsert.executeBatch();

                connection.commit();

                //now retrieve the IDs we just generated
                findSubscriberIdsImpl(serviceId, coreTable, sourceIdsRemaining, ret, connection);
            }

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            connection.close();
        }

        return ret;
    }

    private void findSubscriberIdsImpl(UUID serviceId, byte coreTable, List<String> sourceIds, Map<String, CoreId> map, Connection connection) throws Exception {

        PreparedStatement ps = null;
        try {
            String sql = "SELECT source_id, core_id "
                    + "FROM core_id_map "
                    + "WHERE service_id = ? "
                    + "AND core_table = ? "
                    + "AND source_id IN (";
            for (int i=0; i<sourceIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            ps.setInt(col++, coreTable);
            for (String sourceId: sourceIds) {
                ps.setString(col++, sourceId);
            }
            //LOG.debug("" + ps);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String sourceId = rs.getString(col++);
                int coreId = rs.getInt(col++);

                CoreId o = new CoreId(serviceId, coreTable, coreId, sourceId);
                map.put(sourceId, o);
            }

            rs.close();

        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }


    private static PreparedStatement createAndPopulateUpsertPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        switch (wrapper.getDataType()) {
            case "organization"     :   return createUpsertOrganizationPreparedStatement(connection, wrapper);
            case "patient"          :   return createUpsertPatientPreparedStatement(connection, wrapper);
            case "practitioner"     :   return createUpsertPractitionerPreparedStatement(connection, wrapper);
            case "encounter"        :   return createUpsertEncounterPreparedStatement(connection, wrapper);
            case "encounter_triple" :   return createUpsertEncounterTriplePreparedStatement(connection, wrapper);
            case "observation"      :   return createUpsertObservationPreparedStatement(connection, wrapper);

            default: throw new IllegalArgumentException("CoreFilerWrapper dataType not recognised: "+wrapper.getDataType());
        }
    }

    private static PreparedStatement createUpsertOrganizationPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO organization"
                + " (id, ods_code, name, type_id, postcode, parent_organization_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " ods_code = VALUES(ods_code),"
                + " name = VALUES(name),"
                + " type_id = VALUES(type_id),"
                + " postcode = VALUES(postcode),"
                + " parent_organization_id = VALUES(parent_organization_id) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        Organization organization = (Organization) wrapper.getData();
        int col = 1;
        ps.setInt(col++, organization.getId());
        ps.setString(col++, organization.getOdsCode());
        ps.setString(col++, organization.getName());
        ps.setInt(col++, organization.getTypeId());
        ps.setString(col++, organization.getPostCode());
        if (organization.getParentOrganizationId() != null) {
            ps.setInt(col++, organization.getParentOrganizationId());
        } else {
            ps.setNull(col++, Types.INTEGER);
        }

        return ps;
    }

    private static PreparedStatement createUpsertPatientPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO patient"
                + " (id, organization_id, person_id, title, first_names, last_name, gender_type_id, "
                + " nhs_number, date_of_birth, date_of_death, current_address_id, ethnic_code_type_id, "
                + " registered_practice_organization_id, mothers_nhs_number)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " organization_id = VALUES(organization_id),"
                + " person_id = VALUES(person_id),"
                + " title = VALUES(title),"
                + " first_names = VALUES(first_names),"
                + " last_name = VALUES(last_name),"
                + " gender_type_id = VALUES(gender_type_id),"
                + " nhs_number = VALUES(nhs_number),"
                + " date_of_birth = VALUES(date_of_birth),"
                + " date_of_death = VALUES(date_of_death),"
                + " current_address_id = VALUES(current_address_id),"
                + " ethnic_code_type_id = VALUES(ethnic_code_type_id),"
                + " registered_practice_organization_id = VALUES(registered_practice_organization_id),"
                + " mothers_nhs_number = VALUES(mothers_nhs_number) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        Patient patient = (Patient) wrapper.getData();
        int col = 1;
        ps.setInt(col++, patient.getId());
        ps.setInt(col++, patient.getOrganizationId());
        ps.setInt(col++, patient.getPersonId());
        ps.setString(col++, patient.getFirstNames());
        ps.setString(col++, patient.getLastName());
        ps.setInt(col++, patient.getGenderTypeId());
        ps.setString(col++, patient.getNhsNumber());
        ps.setTimestamp(col++, new java.sql.Timestamp(patient.getDateOfBirth().getTime()));

        if (patient.getDateOfDeath() != null) {
            ps.setTimestamp(col++, new java.sql.Timestamp(patient.getDateOfDeath().getTime()));
        } else {
            ps.setNull(col++, Types.TIMESTAMP);
        }
        ps.setInt(col++, patient.getCurrentAddressId());
        ps.setInt(col++, patient.getEthnicCodeTypeId());
        ps.setInt(col++, patient.getRegisteredPracticeOrganizationId());
        ps.setString(col++, patient.getMothersNHSNumber());

        return ps;
    }

    private static PreparedStatement createUpsertPractitionerPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO practitioner"
                + " (id, organization_id, name, role_code, role_desc, type_id)"
                + " VALUES (?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " organization_id = VALUES(organization_id),"
                + " name = VALUES(name),"
                + " role_code = VALUES(role_code),"
                + " role_desc = VALUES(role_desc),"
                + " type_id = VALUES(type_id) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        Practitioner practitioner = (Practitioner) wrapper.getData();
        int col = 1;
        ps.setInt(col++, practitioner.getId());
        ps.setInt(col++, practitioner.getOrganizationId());
        ps.setString(col++, practitioner.getRoleCode());
        ps.setString(col++, practitioner.getRoleDesc());
        ps.setInt(col++, practitioner.getTypeId());

        return ps;
    }

    private static PreparedStatement createUpsertEncounterPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO encounter"
                + " (id, organization_id, patient_id, clinical_effective_date, type_id, parent_encounter_id, additional_data)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " organization_id = VALUES(organization_id),"
                + " patient_id = VALUES(patient_id),"
                + " clinical_effective_date = VALUES(role_code),"
                + " type_id = VALUES(type_id),"
                + " parent_encounter_id = VALUES(parent_encounter_id),"
                + " additional_data = VALUES(additional_data) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        Encounter encounter = (Encounter) wrapper.getData();
        int col = 1;
        ps.setInt(col++, encounter.getId());
        ps.setInt(col++, encounter.getOrganizationId());
        ps.setInt(col++, encounter.getPatientId());
        ps.setTimestamp(col++, new java.sql.Timestamp(encounter.getClinicalEffectiveDate().getTime()));
        ps.setInt(col++, encounter.getTypeId());
        ps.setInt(col++, encounter.getParentEncounterId());
        ps.setString(col++, encounter.getAdditionalData());

        return ps;
    }

    private static PreparedStatement createUpsertEncounterTriplePreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO encounter_triple"
                + " (id, encounter_id, property_id, value_id)"
                + " VALUES (?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " encounter_id = VALUES(encounter_id),"
                + " property_id = VALUES(property_id),"
                + " clinical_effective_date = VALUES(role_code),"
                + " value_id = VALUES(value_id) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        EncounterTriple encounterTriple = (EncounterTriple) wrapper.getData();
        int col = 1;
        ps.setInt(col++, encounterTriple.getId());
        ps.setInt(col++, encounterTriple.getEncounterId());
        ps.setInt(col++, encounterTriple.getPropertyId());
        ps.setInt(col++, encounterTriple.getValueId());

        return ps;
    }

    private static PreparedStatement createUpsertObservationPreparedStatement(Connection connection, CoreFilerWrapper wrapper) throws Exception {

        String sql = "INSERT INTO observation"
                + " (id, organization_id, patient_id, clinical_effective_date, type_id, result_value, encounter_id, "
                + " encounter_section_id, parent_observation_id, additional_data)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " organization_id = VALUES(organization_id),"
                + " patient_id = VALUES(patient_id),"
                + " clinical_effective_date = VALUES(role_code),"
                + " type_id = VALUES(type_id),"
                + " result_value = VALUES(result_value),"
                + " encounter_id = VALUES(encounter_id),"
                + " encounter_section_id = VALUES(encounter_section_id),"
                + " parent_observation_id = VALUES(parent_observation_id),"
                + " additional_data = VALUES(additional_data) ";

        PreparedStatement ps = connection.prepareStatement(sql);;

        Observation observation = (Observation) wrapper.getData();
        int col = 1;
        ps.setInt(col++, observation.getId());
        ps.setInt(col++, observation.getOrganizationId());
        ps.setInt(col++, observation.getPatientId());
        ps.setTimestamp(col++, new java.sql.Timestamp(observation.getClinicalEffectiveDate().getTime()));
        ps.setInt(col++, observation.getTypeId());
        if (observation.getResultValue() != null) {
            ps.setDouble(col++, observation.getResultValue());
        } else {
            ps.setNull(col++, Types.DOUBLE);
        }
        if (observation.getEncounterId() != null) {
            ps.setInt(col++, observation.getEncounterId());
        } else {
            ps.setNull(col++, Types.INTEGER);
        }
        if (observation.getEncounterSectionId() != null) {
            ps.setInt(col++, observation.getEncounterSectionId());
        } else {
            ps.setNull(col++, Types.INTEGER);
        }
        if (observation.getParentObservationId() != null) {
            ps.setInt(col++, observation.getParentObservationId());
        } else {
            ps.setNull(col++, Types.INTEGER);
        }
        ps.setString(col++, observation.getAdditionalData());

        return ps;
    }







//    /**
//     * logical delete, when we want to delete a resource but maintain our audits
//     */
//    @Override
//    public void delete(ResourceWrapper resourceEntry) throws Exception {
//        List<ResourceWrapper> l = new ArrayList<>();
//        l.add(resourceEntry);
//        delete(l);
//    }
//
//
//    /**
//     * physical delete, when we want to remove all trace of data from DDS
//     *
//     * only needs to delete from resource_current as resource_history will be sorted by the trigger
//     */
//    @Override
//    public void hardDeleteResourceAndAllHistory(ResourceWrapper wrapper) throws Exception {
//
//        UUID serviceId = wrapper.getServiceId();
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//
//        try {
//            String sql = "DELETE FROM resource_current"
//                    + " WHERE service_id = ?"
//                    + " AND patient_id = ?"
//                    + " AND resource_type = ?"
//                    + " AND resource_id = ?";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, wrapper.getServiceId().toString());
//            if (wrapper.getPatientId() != null) {
//                ps.setString(col++, wrapper.getPatientId().toString());
//            } else {
//                ps.setString(col++, ""); //DB field doesn't allow nulls so save as empty String
//            }
//            ps.setString(col++, wrapper.getResourceType());
//            ps.setString(col++, wrapper.getResourceId().toString());
//
//            ps.executeUpdate();
//
//            connection.commit();
//
//        } catch (Exception ex) {
//            connection.rollback();
//            throw ex;
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }


//    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " AND patient_id = ?";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            if (patientId != null) {
//                ps.setString(col++, patientId.toString());
//            } else {
//                //for admin resourecs, the patient ID will be a non-null empty string
//                ps.setString(col++, "");
//            }
//
//            ResultSet rs = ps.executeQuery();
//            return readResourceWrappersFromResourceCurrentResultSet(rs, false);
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//
//    }

//    @Override
//    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId, String resourceType) throws Exception {
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " AND patient_id = ?"
//                    + " AND resource_type = ?";
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            ps.setString(col++, patientId.toString());
//            ps.setString(col++, resourceType.toString());
//
//            ResultSet rs = ps.executeQuery();
//            return readResourceWrappersFromResourceCurrentResultSet(rs, false);
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }

//    @Override
//    public List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception {
//
//        if (resourceIds.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " AND resource_type = ?"
//                    + " AND resource_id IN (";
//            for (int i=0; i<resourceIds.size(); i++) {
//                if (i>0) {
//                    sql += ", ";
//                }
//                sql += "?";
//            }
//            sql += ")";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            ps.setString(col++, resourceType);
//            for (int i=0; i<resourceIds.size(); i++) {
//                UUID resourceId = resourceIds.get(i);
//                ps.setString(col++, resourceId.toString());
//            }
//
//            ResultSet rs = ps.executeQuery();
//            return readResourceWrappersFromResourceCurrentResultSet(rs, false);
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }


    /**
     * getResourcesForBatch(..) returns the resources as they exactly were when the batch was created,
     * so can return an older version than is currently on the DB. This function returns the CURRENT version
     * of each resource that's in the batch.
     */
//    @Override
//    public List<ResourceWrapper> getCurrentVersionOfResourcesForBatch(UUID serviceId, UUID batchId) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            //deleted resources are removed from resource_current, so we need a left outer join
//            //and have to select enough columns from resource_history to be able to spot the deleted
//            //resources and create resource wrappers for them
//            //changed the order of columns so that we never get a null string in the last column,
//            //which exposed a bug in the MySQL driver (https://bugs.mysql.com/bug.php?id=84084)
//            String sql = "SELECT h.service_id, h.system_id, h.patient_id, "
//                    + " c.system_id, c.updated_at, c.patient_id, c.resource_data, h.resource_type, h.resource_id"
//                    + " FROM resource_history h"
//                    + " LEFT OUTER JOIN resource_current c"
//                    + " ON h.resource_id = c.resource_id"
//                    + " AND h.resource_type = c.resource_type"
//                    + " WHERE h.exchange_batch_id = ?";
//
//            ps = connection.prepareStatement(sql);
//            ps.setString(1, batchId.toString());
//
//            List<ResourceWrapper> ret = new ArrayList<>();
//
//            //some transforms can end up saving the same resource multiple times in a batch, so we'll have duplicate
//            //rows in our resource_history table. Since they all join to the latest data in resource_current table, we
//            //don't need to worry about which we keep, so just use this to avoid duplicates
//            Set<String> resourcesDone = new HashSet<>();
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                int col = 1;
//                String historyServiceId = rs.getString(col++);
//                String historySystemId = rs.getString(col++);
//                /*String historyResourceType = rs.getString(col++);
//                String historyResourceId = rs.getString(col++);*/
//                String historyPatientId = rs.getString(col++);
//
//                //since we're not dealing with any primitive types, we can just use getString(..)
//                //and check that the result is null or not, without needing to use wasNull(..)
//                String currentSystemId = rs.getString(col++);
//                Date currentUpdatedAt = null;
//                java.sql.Timestamp ts = rs.getTimestamp(col++);
//                if (ts != null) {
//                    currentUpdatedAt = new Date(ts.getTime());
//                }
//                String currentPatientId = rs.getString(col++);
//                String currentResourceData = rs.getString(col++);
//
//                //moved these columns to be the last in the result set, to avoid MySQL bug https://bugs.mysql.com/bug.php?id=84084
//                String historyResourceType = rs.getString(col++);
//                String historyResourceId = rs.getString(col++);
//
//                //skip if we've already done this resource
//                String referenceStr = ReferenceHelper.createResourceReference(historyResourceType, historyResourceId);
//                if (resourcesDone.contains(referenceStr)) {
//                    continue;
//                }
//                resourcesDone.add(referenceStr);
//
//                //populate the resource wrapper with what we've got, depending on what's null or not.
//                //NOTE: the resource wrapper will have the following fields null:
//                //UUID version;
//                //Date createdAt;
//                //String resourceMetadata;
//                //Long resourceChecksum;
//                //UUID exchangeId;
//
//                ResourceWrapper wrapper = new ResourceWrapper();
//                wrapper.setServiceId(UUID.fromString(historyServiceId));
//                wrapper.setResourceType(historyResourceType);
//                wrapper.setResourceId(UUID.fromString(historyResourceId));
//                wrapper.setExchangeBatchId(batchId); //not sure about setting this... seems to imply that this is the current batch ID
//
//                //if we have no resource data, the resource is deleted, so populate with what we've got from the history table
//                if (currentResourceData == null) {
//                    wrapper.setDeleted(true);
//                    wrapper.setSystemId(UUID.fromString(historySystemId));
//                    if (!Strings.isNullOrEmpty(historyPatientId)) {
//                        wrapper.setPatientId(UUID.fromString(historyPatientId));
//                    }
//
//                } else {
//                    //if we have resource data, then populate with what we've got from resource_current
//                    wrapper.setSystemId(UUID.fromString(currentSystemId));
//                    wrapper.setResourceData(currentResourceData);
//                    wrapper.setCreatedAt(currentUpdatedAt);
//                    if (!Strings.isNullOrEmpty(currentPatientId)) {
//                        wrapper.setPatientId(UUID.fromString(currentPatientId));
//                    }
//                }
//
//                ret.add(wrapper);
//            }
//
//            return ret;
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }



    /**
     * tests if we have any patient data stored for the given service
     * note this purposefully doesn't exclude "deleted" resource_current records since it's used to test
     * for the absolute presence of any data rather than whether there's data that has been logically deleted or not
     */
//    public boolean dataExists(UUID serviceId) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " LIMIT 1";
//
//            ps = connection.prepareStatement(sql);
//
//            ps.setString(1, serviceId.toString());
//
//            ResultSet rs = ps.executeQuery();
//            List<ResourceWrapper> l = readResourceWrappersFromResourceCurrentResultSet(rs, true);
//            return !l.isEmpty();
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }
//
//    public ResourceWrapper getFirstResourceByService(UUID serviceId, ResourceType resourceType) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " AND resource_type = ?"
//                    + " AND resource_data IS NOT NULL" //specifically want to get a non-deleted resource
//                    + " ORDER BY updated_at ASC"
//                    + " LIMIT 1";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            ps.setString(col++, resourceType.toString());
//
//            ResultSet rs = ps.executeQuery();
//            List<ResourceWrapper> l = readResourceWrappersFromResourceCurrentResultSet(rs, false);
//            if (l.isEmpty()) {
//                return null;
//            } else {
//                return l.get(0);
//            }
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }
//
//    public List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = getResourceCurrentSelectPrefix()
//                    + " WHERE service_id = ?"
//                    + " AND resource_type = ?";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            ps.setString(col++, resourceType);
//
//            ResultSet rs = ps.executeQuery();
//            return readResourceWrappersFromResourceCurrentResultSet(rs, false);
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }
//
//    /**
//     * returns a count of resources for a given service and type
//     * note that this includes deleted resources in the count, since this is just for statistics purposes
//     */
//    @Override
//    public long getResourceCountByService(UUID serviceId, String resourceType) throws Exception {
//
//        Connection connection = ConnectionManager.getEhrConnection(serviceId);
//        PreparedStatement ps = null;
//        try {
//            String sql = "SELECT COUNT(1)"
//                    + " FROM resource_current"
//                    + " WHERE service_id = ?"
//                    + " AND resource_type = ?";
//
//            ps = connection.prepareStatement(sql);
//
//            int col = 1;
//            ps.setString(col++, serviceId.toString());
//            ps.setString(col++, resourceType);
//
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                return rs.getLong(1);
//            } else {
//                return 0;
//            }
//
//        } finally {
//            if (ps != null) {
//                ps.close();
//            }
//            connection.close();
//        }
//    }
}
