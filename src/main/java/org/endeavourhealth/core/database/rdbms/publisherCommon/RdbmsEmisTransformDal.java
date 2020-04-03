package org.endeavourhealth.core.database.rdbms.publisherCommon;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCodeType;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCsvCodeMap;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisMissingCodes;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceFieldMappingAudit;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisAdminResourceCache;
import org.endeavourhealth.core.database.rdbms.publisherCommon.models.RdbmsEmisAdminResourceCacheApplied;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsEmisTransformDal implements EmisTransformDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsEmisTransformDal.class);

    @Override
    public void saveCodeMappings( List<EmisCsvCodeMap> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("mappings is null or empty");
        }

        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySaveCodeMappings(mappings);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }

    }

    public void trySaveCodeMappings(List<EmisCsvCodeMap> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("Trying to save null or empty mappings");
        }

        //ensure all mappings are meds or not
        Boolean medication = null;
        Map<Long, EmisCsvCodeMap> hmMappings = new HashMap<>();

        for (EmisCsvCodeMap mapping: mappings) {
            if (medication == null) {
                medication = new Boolean(mapping.isMedication());
            } else if (medication.booleanValue() != mapping.isMedication()) {
                throw new Exception("Must be saving all medications or all non-medications");
            }

            hmMappings.put(new Long(mapping.getCodeId()), mapping);
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;

        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT code_id, dt_last_received"
                    + " FROM emis_csv_code_map"
                    + " WHERE medication = ?"
                    + " AND code_id IN (";
            for (int i=0; i<mappings.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";
            psSelect = connection.prepareStatement(sql);

            int col = 1;
            psSelect.setBoolean(col++, medication.booleanValue());
            for (EmisCsvCodeMap mapping: mappings) {
                psSelect.setLong(col++, mapping.getCodeId());
            }

            ResultSet rs = psSelect.executeQuery();
            while (rs.next()) {
                col = 1;
                long codeId = rs.getLong(col++);
                Date dtLastReceived = null;
                Timestamp ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    dtLastReceived = new Date(ts.getTime());
                }

                EmisCsvCodeMap mapping = hmMappings.get(new Long(codeId));

                //if the one already on the DB has a date and that date is the same or
                //later than the one we're trying to save, then SKIP the one we're saving
                if (dtLastReceived != null
                        && !mapping.getDtLastReceived().after(dtLastReceived)) {

                    hmMappings.remove(new Long(codeId));
                }
            }

            //if there's nothing new to save, return out
            if (hmMappings.isEmpty()) {
                return;
            }

            sql = "INSERT INTO emis_csv_code_map"
                    + " (medication, code_id, code_type, read_term, read_code, snomed_concept_id, snomed_description_id, snomed_term, national_code, national_code_category, national_code_description, parent_code_id, audit_json, dt_last_received, adjusted_code, codeable_concept_system)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " code_type = VALUES(code_type),"
                    + " read_term = VALUES(read_term),"
                    + " read_code = VALUES(read_code),"
                    + " snomed_concept_id = VALUES(snomed_concept_id),"
                    + " snomed_description_id = VALUES(snomed_description_id),"
                    + " snomed_term = VALUES(snomed_term),"
                    + " national_code = VALUES(national_code),"
                    + " national_code_category = VALUES(national_code_category),"
                    + " national_code_description = VALUES(national_code_description),"
                    + " parent_code_id = VALUES(parent_code_id),"
                    + " audit_json = VALUES(audit_json),"
                    + " dt_last_received = VALUES(dt_last_received),"
                    + " adjusted_code = VALUES(adjusted_code),"
                    + " codeable_concept_system = VALUES(codeable_concept_system)";

            psInsert = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (Long codeId: hmMappings.keySet()) {
                EmisCsvCodeMap mapping = hmMappings.get(new Long(codeId));

                col = 1;
                psInsert.setBoolean(col++, mapping.isMedication());
                psInsert.setLong(col++, mapping.getCodeId());
                if (Strings.isNullOrEmpty(mapping.getCodeType())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getCodeType());
                }
                if (Strings.isNullOrEmpty(mapping.getReadTerm())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getReadTerm());
                }
                if (Strings.isNullOrEmpty(mapping.getReadCode())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getReadCode());
                }
                if (mapping.getSnomedConceptId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getSnomedConceptId());
                }
                if (mapping.getSnomedDescriptionId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getSnomedDescriptionId());
                }
                if (Strings.isNullOrEmpty(mapping.getSnomedTerm())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getSnomedTerm());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCode())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCode());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCodeCategory())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCodeCategory());
                }
                if (Strings.isNullOrEmpty(mapping.getNationalCodeDescription())) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getNationalCodeDescription());
                }
                if (mapping.getParentCodeId() == null) {
                    psInsert.setNull(col++, Types.BIGINT);
                } else {
                    psInsert.setLong(col++, mapping.getParentCodeId());
                }
                if (mapping.getAudit() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getAudit().writeToJson());
                }
                if (mapping.getDtLastReceived() == null) {
                    psInsert.setNull(col++, Types.TIMESTAMP);
                } else {
                    psInsert.setTimestamp(col++, new Timestamp(mapping.getDtLastReceived().getTime()));
                }
                if (mapping.getAdjustedCode() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getAdjustedCode());
                }
                if (mapping.getCodeableConceptSystem() == null) {
                    psInsert.setNull(col++, Types.VARCHAR);
                } else {
                    psInsert.setString(col++, mapping.getCodeableConceptSystem());
                }

                psInsert.addBatch();
            }

            psInsert.executeBatch();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psSelect != null) {
                psSelect.close();
            }
            if (psInsert != null) {
                psInsert.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void saveCodeMapping(EmisCsvCodeMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        List<EmisCsvCodeMap>l = new ArrayList<>();
        l.add(mapping);
        saveCodeMappings(l);
    }

    @Override
    public EmisCsvCodeMap getCodeMapping(boolean medication, Long codeId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "SELECT medication, code_id, code_type, read_term, read_code, snomed_concept_id, "
                        + "snomed_description_id, snomed_term, national_code, national_code_category, "
                        + "national_code_description, parent_code_id, audit_json, dt_last_received, adjusted_code, codeable_concept_system "
                        + "FROM emis_csv_code_map "
                        + "WHERE medication = ? "
                        + "AND code_id = ?";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setBoolean(col++, medication);
            ps.setLong(col++, codeId.longValue());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EmisCsvCodeMap ret = new EmisCsvCodeMap();

                col = 1;
                ret.setMedication(rs.getBoolean(col++));
                ret.setCodeId(rs.getLong(col++));
                ret.setCodeType(rs.getString(col++));
                ret.setReadTerm(rs.getString(col++));
                ret.setReadCode(rs.getString(col++));

                //have to use isNull for this field because it returns a primitive long
                long snomedConceptId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedConceptId(new Long(snomedConceptId));
                }

                long snomedDescriptionId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setSnomedDescriptionId(new Long(snomedDescriptionId));
                }

                ret.setSnomedTerm(rs.getString(col++));
                ret.setNationalCode(rs.getString(col++));
                ret.setNationalCodeCategory(rs.getString(col++));
                ret.setNationalCodeDescription(rs.getString(col++));

                long parentCodeId = rs.getLong(col++);
                if (!rs.wasNull()) {
                    ret.setParentCodeId(new Long(parentCodeId));
                }

                String auditJson = rs.getString(col++);
                if (!rs.wasNull()) {
                    ret.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                Timestamp ts = rs.getTimestamp(col++);
                if (!rs.wasNull()) {
                    ret.setDtLastReceived(new Date(ts.getTime()));
                }

                ret.setAdjustedCode(rs.getString(col++));
                ret.setCodeableConceptSystem(rs.getString(col++));

                return ret;

            } else {
                  return null;
            }


        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    public void saveAdminResource(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {

            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());
            ps.setString(4, emisObj.getResourceData());
            ps.setString(5, emisObj.getAuditJson());
            //entityManager.persist(emisObj);

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

    public void deleteAdminResource(EmisAdminResourceCache resourceCache) throws Exception {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        RdbmsEmisAdminResourceCache emisObj = new RdbmsEmisAdminResourceCache(resourceCache);

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.remove(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND emis_guid = ?"
                    + " AND resource_type = ?";

            ps = connection.prepareStatement(sql);

            ps.setString(1, emisObj.getDataSharingAgreementGuid());
            ps.setString(2, emisObj.getEmisGuid());
            ps.setString(3, emisObj.getResourceType());

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

    @Override
    public void saveAdminResources(List<EmisAdminResourceCache> resources) throws Exception {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("resources is null or empty");
        }

        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySaveAdminResources(resources);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    public void trySaveAdminResources(List<EmisAdminResourceCache> resources) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;

        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO emis_admin_resource_cache"
                    + " (data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " resource_data = VALUES(resource_data),"
                    + " audit_json = VALUES(audit_json)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (EmisAdminResourceCache resource: resources) {

                int col = 1;
                ps.setString(col++, resource.getDataSharingAgreementGuid());
                ps.setString(col++, resource.getEmisGuid());
                ps.setString(col++, resource.getResourceType());
                ps.setString(col++, resource.getResourceData());
                if (resource.getAudit() != null) {
                    ps.setString(col++, resource.getAudit().writeToJson());
                } else {
                    ps.setNull(col++, Types.VARCHAR);
                }

                ps.addBatch();
            }

            ps.executeBatch();

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

    @Override
    public void deleteAdminResources(List<EmisAdminResourceCache> resources) throws Exception {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("resources is null or empty");
        }

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        PreparedStatement ps = null;
        try {

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.remove(emisObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND emis_guid = ?"
                    + " AND resource_type = ?";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            for (EmisAdminResourceCache resource: resources) {

                int col = 1;
                ps.setString(col++, resource.getDataSharingAgreementGuid());
                ps.setString(col++, resource.getEmisGuid());
                ps.setString(col++, resource.getResourceType());

                ps.addBatch();
            }

            ps.executeBatch();

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


    @Override
    public EmisAdminResourceCache getAdminResource(String dataSharingAgreementGuid, ResourceType resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCache c"
                    + " where c.dataSharingAgreementGuid = :data_sharing_agreement_guid"
                    + " and c.resourceType = :resource_type"
                    + " and c.emisGuid = :emis_guid";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCache.class)
                    .setParameter("data_sharing_agreement_guid", dataSharingAgreementGuid)
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("emis_guid", sourceId);

            try {
                RdbmsEmisAdminResourceCache result = (RdbmsEmisAdminResourceCache)query.getSingleResult();
                return new EmisAdminResourceCache(result);

            } catch (NoResultException ex) {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Map<String, EmisAdminResourceCache> getAdminResources(String dataSharingAgreementGuid,
                                                                 ResourceType resourceType, List<String> sourceIds) throws Exception {

        if (sourceIds.isEmpty()) {
            throw new Exception("Source IDs cannot be empty");
        }

        Connection connection = ConnectionManager.getPublisherCommonConnection();

        PreparedStatement ps = null;
        try {
            String sql = "SELECT data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json"
                    + " FROM emis_admin_resource_cache"
                    + " WHERE data_sharing_agreement_guid = ?"
                    + " AND resource_type = ?"
                    + " AND emis_guid IN (";
            for (int i=0; i<sourceIds.size(); i++) {
                if (i>0) {
                    sql += ", ";
                }
                sql += "?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, dataSharingAgreementGuid);
            ps.setString(col++, resourceType.toString());
            for (int i=0; i<sourceIds.size(); i++) {
                ps.setString(col++, sourceIds.get(i));
            }

            Map<String, EmisAdminResourceCache> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                col = 1;
                String sharingGuid = rs.getString(col++);
                String emisGuid = rs.getString(col++);
                String type = rs.getString(col++);
                String resourceData = rs.getString(col++);
                String auditJson = rs.getString(col++);

                EmisAdminResourceCache o = new EmisAdminResourceCache();
                o.setDataSharingAgreementGuid(sharingGuid);
                o.setEmisGuid(emisGuid);
                o.setResourceType(type);
                o.setResourceData(resourceData);
                if (!Strings.isNullOrEmpty(auditJson)) {
                    o.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
                }

                ret.put(emisGuid, o);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
    }

    private EntityManager adminCacheRetrieveEntityManager;
    private PreparedStatement adminCacheRetrievePreparedStatement;
    private ResultSet adminCacheRetrieveResultSet;


    @Override
    public void startRetrievingAdminResources(String dataSharingAgreementGuid) throws Exception {

        if (adminCacheRetrieveEntityManager != null) {
            throw new Exception("Already retreiving admin resources");
        }

        adminCacheRetrieveEntityManager = ConnectionManager.getPublisherCommonEntityManager();

        SessionImpl session = (SessionImpl)adminCacheRetrieveEntityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "SELECT data_sharing_agreement_guid, emis_guid, resource_type, resource_data, audit_json"
                + " FROM emis_admin_resource_cache"
                + " WHERE data_sharing_agreement_guid = ?";

        adminCacheRetrievePreparedStatement = connection.prepareStatement(sql);
        adminCacheRetrievePreparedStatement.setFetchSize(10000); //only retrieve a limited amount at a time
        adminCacheRetrievePreparedStatement.setString(1, dataSharingAgreementGuid);

        adminCacheRetrieveResultSet = adminCacheRetrievePreparedStatement.executeQuery();
    }

    @Override
    public EmisAdminResourceCache getNextAdminResource() throws Exception {

        if (adminCacheRetrieveResultSet == null) {
            throw new Exception("Haven't started retrieving admin resources");
        }

        if (adminCacheRetrieveResultSet.next()) {

            int col = 1;

            EmisAdminResourceCache ret = new EmisAdminResourceCache();
            ret.setDataSharingAgreementGuid(adminCacheRetrieveResultSet.getString(col++));
            ret.setEmisGuid(adminCacheRetrieveResultSet.getString(col++));
            ret.setResourceType(adminCacheRetrieveResultSet.getString(col++));
            ret.setResourceData(adminCacheRetrieveResultSet.getString(col++));

            String auditJson = adminCacheRetrieveResultSet.getString(col++);
            if (auditJson != null) {
                ret.setAudit(ResourceFieldMappingAudit.readFromJson(auditJson));
            }

            return ret;

        } else {

            //if we've reeached the end, then close everything down
            adminCacheRetrievePreparedStatement.close();
            adminCacheRetrieveEntityManager.close();

            //and null everything out
            adminCacheRetrievePreparedStatement = null;
            adminCacheRetrieveEntityManager = null;
            adminCacheRetrieveResultSet = null;

            return null;
        }
    }

    @Override
    public boolean wasAdminCacheApplied(UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEmisAdminResourceCacheApplied c"
                    + " where c.serviceId = :service_id";

            Query query = entityManager.createQuery(sql, RdbmsEmisAdminResourceCacheApplied.class)
                    .setParameter("service_id", serviceId.toString());

            try {
                RdbmsEmisAdminResourceCacheApplied result = (RdbmsEmisAdminResourceCacheApplied)query.getSingleResult();
                return true;

            } catch (NoResultException ex) {
                return false;
            }

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void adminCacheWasApplied(UUID serviceId, String dataSharingAgreementGuid) throws Exception {

        RdbmsEmisAdminResourceCacheApplied o = new RdbmsEmisAdminResourceCacheApplied();
        o.setServiceId(serviceId.toString());
        o.setDataSharingAgreementGuid(dataSharingAgreementGuid);
        o.setDateApplied(new Date());

        EntityManager entityManager = ConnectionManager.getPublisherCommonEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(o);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void saveErrorRecords(EmisMissingCodes errorCodeVals) throws Exception {

        Connection connection = ConnectionManager.getPublisherCommonConnection();
        PreparedStatement psInsert = null;
        try {
            Date now = new Date();

            //use insert ignore so that if we re-process the same file (without any fixed data)
            //we don't have any issues when it tries to log the error again
            String sql = "INSERT IGNORE INTO emis_missing_code_error"
                    + " (service_id, exchange_id, timestmp, file_type, patient_guid, code_id, record_guid, dt_fixed, code_type)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

            psInsert = connection.prepareStatement(sql);
            int col = 1;
            psInsert.setString(col++, errorCodeVals.getServiceId().toString());
            psInsert.setString(col++, errorCodeVals.getExchangeId().toString());
            psInsert.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
            psInsert.setString(col++, errorCodeVals.getFileType());
            psInsert.setString(col++, errorCodeVals.getPatientGuid());
            psInsert.setLong(col++, errorCodeVals.getCodeId());
            psInsert.setString(col++, errorCodeVals.getRecordGuid());
            psInsert.setTimestamp(col++, null);
            psInsert.setString(col++, errorCodeVals.getCodeType().getCodeValue());

            psInsert.executeUpdate();
            connection.commit();

        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (psInsert != null) {
                psInsert.close();
            }
            connection.close();
        }
    }

    public List<String> retrieveEmisPatientGuids(List<String> emisMissingCodes) throws Exception {

        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        List<String> emisPatientGuids = new ArrayList<String>();
        try {
            LOG.info("retrieveEmisPatientGuids codes size :: " + emisMissingCodes.size());
            String sql = "select distinct patient_guid from emis_missing_code_error  where code_id in (";
            String joinEmisCodes = StringUtils.join(emisMissingCodes, ",");
            sql += joinEmisCodes + ")";
            sql += " and dt_fixed is null ";
            LOG.info("retrieveEmisPatientGuids sql :: " + sql.toString());
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String patientGuid = rs.getString("patient_guid");
                emisPatientGuids.add(patientGuid);
                LOG.info("Retrieved EmisPatientGuids size :: " + emisPatientGuids.size());
            }
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
        return emisPatientGuids;
    }

    public String retrieveEmisOldestExchangeId(List<String> emisMissingCodes) throws Exception {

        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        String emisOldestExchangeId = null;

        try {
            LOG.info("retrieveEmisOldestExchangeId EmisMissing codes size :: " + emisMissingCodes.size());
            String sql = "select exchange_id from emis_missing_code_error where timestmp=(select min(timestmp) from emis_missing_code_error where code_id in (";
            String joinEmisCodes = StringUtils.join(emisMissingCodes,",");
            sql += joinEmisCodes+")";
            sql += " and dt_fixed is null)";
            LOG.info("retrieveEmisOldestExchangeId sql query :: " + sql.toString());
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                emisOldestExchangeId = rs.getString("exchange_id");
                LOG.info("EmisOldest ExchangeId:: " + emisOldestExchangeId);
            }
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
        return emisOldestExchangeId;
    }

    public List<String> retrieveEmisMissingCodeList(EmisCodeType emisCodeType) throws Exception {

        LOG.info("retrieveEmisMissingCodeList EmisCodeTypeValue:: " + emisCodeType.getCodeValue());
        Connection connection = ConnectionManager.getAuditConnection();
        PreparedStatement ps = null;
        List<String> emisMissingCodeList = new ArrayList<String>();

        try {
            String sql = "SELECT  distinct code_id from emis_missing_code_error where dt_fixed is null and code_type= ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, emisCodeType.getCodeValue());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                emisMissingCodeList.add(String.valueOf(rs.getLong("code_id")));
            }
        } catch (Exception ex) {
            connection.rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            connection.close();
        }
        return emisMissingCodeList;
    }

    public void updateStatusInEmisErrorTable(List<String> emisCombinedClinicalDrugCodes) throws Exception {

        Connection connection = ConnectionManager.getAuditConnection();
        Date now = new Date();
        PreparedStatement ps = null;

        try {
            String sql = "update emis_missing_code_error SET dt_fixed =? where dt_fixed is null and code_id in (";
            emisCombinedClinicalDrugCodes.stream().collect(Collectors.joining("','", "'", "'"));
            String collectEmisCombinedClinicalDrugCodes = emisCombinedClinicalDrugCodes.stream().collect(Collectors.joining("','", "'", "'"));
            String joinEmisCombinedClinicalDrugCodes = StringUtils.join(collectEmisCombinedClinicalDrugCodes);
            sql += joinEmisCombinedClinicalDrugCodes + ")";
            LOG.info("updateStatusInEmisErrorTable sql query :: " + sql.toString());
            ps = connection.prepareStatement(sql);
            int col = 1;
            ps.setTimestamp(col++, new java.sql.Timestamp(now.getTime()));
            int i = ps.executeUpdate();
            connection.commit();
            LOG.info("updateStatusInEmisErrorTable Update Status :: " + i);
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
}
