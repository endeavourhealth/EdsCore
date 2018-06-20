package org.endeavourhealth.core.database.rdbms.ehr;

import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceMetadataIterator;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceCurrent;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceHistory;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class RdbmsResourceDal implements ResourceDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceDal.class);

    private static final ParserPool PARSER_POOL = new ParserPool();

    /*private static CallableStatementCache saveResourceStatementCache = new CallableStatementCache("{call save_resource(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
    private static CallableStatementCache deleteResourceStatementCache = new CallableStatementCache("{call delete_resource(?, ?, ?, ?, ?, ?, ?, ?)}");
    private static CallableStatementCache physicalDeleteResourceStatementCache = new CallableStatementCache("{call physical_delete_resource(?, ?, ?, ?, ?, ?, ?)}");*/

    //private static Map<UUID, Integer> counts = new HashMap<>();

    public void save(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory(resourceEntry);
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent(resourceEntry);

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        //CallableStatement storedProcedure = null;
        PreparedStatement psResourceHistory = null;
        PreparedStatement psResourceCurrent = null;

        try {
            entityManager.getTransaction().begin();

            //we want to do an INSERT ... ON DUPLICATE KEY UPDATE, which isn't possible through JPA, so
            //I'm just dropping to using a prepared statement to save it, which means we don't need to perform
            //a read before each write as it's all handled in MySQL
            //entityManager.persist(resourceCurrent);
            //entityManager.persist(resourceHistory);

            /*storedProcedure = createAndPopulateSaveResourceCallableStatement(entityManager, resourceEntry);
            storedProcedure.execute();*/

            psResourceHistory = createAndPopulateInsertResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createAndPopulateInsertResourceCurrentPreparedStatement(entityManager, resourceCurrent);
            psResourceCurrent.executeUpdate();

            entityManager.getTransaction().commit();


            /*if (resourceEntry.getResourceType().equals("Organization")) {
                Integer integer = counts.get(serviceId);
                if (integer == null) {
                    integer = new Integer(1);
                } else {
                    integer = new Integer(integer.intValue()+1);
                }
                counts.put(serviceId, integer);
                LOG.trace("Saved " + integer + " orgs for " + serviceId);
            }*/


        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            //saveResourceStatementCache.returnCallableStatement(entityManager, storedProcedure);
            if (psResourceCurrent != null) {
                psResourceCurrent.close();
            }
            if (psResourceHistory != null) {
                psResourceHistory.close();
            }
            entityManager.close();
        }
    }

    /*private CallableStatement createAndPopulateSaveResourceCallableStatement(EntityManager entityManager, ResourceWrapper resourceEntry) throws Exception {

        CallableStatement cs = saveResourceStatementCache.getCallableStatement(entityManager);

        cs.setString(1, resourceEntry.getServiceId().toString());
        cs.setString(2, resourceEntry.getSystemId().toString());
        cs.setString(3, resourceEntry.getResourceType());
        cs.setString(4, resourceEntry.getResourceId().toString());
        cs.setTimestamp(5, new java.sql.Timestamp(resourceEntry.getCreatedAt().getTime()));
        if (resourceEntry.getPatientId() != null) {
            cs.setString(6, resourceEntry.getPatientId().toString());
        } else {
            //patient ID is used in one of the indexes so can't be null
            cs.setString(6, "");
        }
        cs.setString(7, resourceEntry.getResourceData());
        cs.setLong(8, resourceEntry.getResourceChecksum());
        cs.setString(9, resourceEntry.getExchangeBatchId().toString());
        cs.setString(10, resourceEntry.getVersion().toString());
        cs.setString(11, resourceEntry.getResourceMetadata());

        return cs;
    }

    private CallableStatement createAndPopulateDeleteResourceCallableStatement(EntityManager entityManager, ResourceWrapper resourceEntry) throws Exception {
        CallableStatement cs = deleteResourceStatementCache.getCallableStatement(entityManager);

        cs.setString(1, resourceEntry.getServiceId().toString());
        cs.setString(2, resourceEntry.getSystemId().toString());
        cs.setString(3, resourceEntry.getResourceType());
        cs.setString(4, resourceEntry.getResourceId().toString());
        cs.setTimestamp(5, new java.sql.Timestamp(resourceEntry.getCreatedAt().getTime()));
        if (resourceEntry.getPatientId() != null) {
            cs.setString(6, resourceEntry.getPatientId().toString());
        } else {
            //patient ID is used in one of the indexes so can't be null
            cs.setString(6, "");
        }
        cs.setString(7, resourceEntry.getExchangeBatchId().toString());
        cs.setString(8, resourceEntry.getVersion().toString());

        return cs;
    }

    private CallableStatement createAndPopulatePhysicalDeleteCallableStatement(EntityManager entityManager, ResourceWrapper resourceEntry) throws Exception {
        CallableStatement cs = physicalDeleteResourceStatementCache.getCallableStatement(entityManager);

        cs.setString(1, resourceEntry.getServiceId().toString());
        cs.setString(2, resourceEntry.getSystemId().toString());
        cs.setString(3, resourceEntry.getResourceType());
        cs.setString(4, resourceEntry.getResourceId().toString());
        cs.setTimestamp(5, new java.sql.Timestamp(resourceEntry.getCreatedAt().getTime()));
        if (resourceEntry.getPatientId() != null) {
            cs.setString(6, resourceEntry.getPatientId().toString());
        } else {
            //patient ID is used in one of the indexes so can't be null
            cs.setString(6, "");
        }
        cs.setString(7, resourceEntry.getVersion().toString());

        return cs;
    }*/



    private static PreparedStatement createInsertResourceCurrentPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO resource_current"
                + " (service_id, system_id, resource_type, resource_id, updated_at, patient_id, resource_data, resource_checksum, resource_metadata)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
                + " patient_id = VALUES(patient_id)," //although part of the primary key, the unique index on the table means we can update the patient_id on a resource with this
                + " system_id = VALUES(system_id),"
                + " updated_at = VALUES(updated_at),"
                + " resource_data = VALUES(resource_data),"
                + " resource_checksum = VALUES(resource_checksum),"
                + " resource_metadata = VALUES(resource_metadata);";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createAndPopulateInsertResourceCurrentPreparedStatement(EntityManager entityManager, RdbmsResourceCurrent resourceCurrent) throws Exception {

        PreparedStatement ps = createInsertResourceCurrentPreparedStatement(entityManager);

        ps.setString(1, resourceCurrent.getServiceId());
        ps.setString(2, resourceCurrent.getSystemId());
        ps.setString(3, resourceCurrent.getResourceType());
        ps.setString(4, resourceCurrent.getResourceId());
        ps.setTimestamp(5, new java.sql.Timestamp(resourceCurrent.getUpdatedAt().getTime()));
        if (resourceCurrent.getPatientId() != null) {
            ps.setString(6, resourceCurrent.getPatientId());
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        ps.setString(7, resourceCurrent.getResourceData());
        ps.setLong(8, resourceCurrent.getResourceChecksum());
        ps.setString(9, resourceCurrent.getResourceMetadata());

        return ps;
    }

    private static PreparedStatement createInsertResourceHistoryPreparedStatement(EntityManager entityManager) throws Exception {
        
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO resource_history"
                + " (service_id, system_id, resource_type, resource_id, created_at, patient_id, resource_data, resource_checksum, is_deleted, exchange_batch_id, version)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //note this entity is always inserted, never updated, so there's no handler for errors with an insert, like resource_current

        return connection.prepareStatement(sql);
    }
    
    private static PreparedStatement createAndPopulateInsertResourceHistoryPreparedStatement(EntityManager entityManager, RdbmsResourceHistory resourceHistory) throws Exception {

        PreparedStatement ps = createInsertResourceHistoryPreparedStatement(entityManager);

        ps.setString(1, resourceHistory.getServiceId());
        ps.setString(2, resourceHistory.getSystemId());
        ps.setString(3, resourceHistory.getResourceType());
        ps.setString(4, resourceHistory.getResourceId());
        ps.setTimestamp(5, new java.sql.Timestamp(resourceHistory.getCreatedAt().getTime()));
        if (!Strings.isNullOrEmpty(resourceHistory.getPatientId())) {
            ps.setString(6, resourceHistory.getPatientId());    
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(resourceHistory.getResourceData())) {
            ps.setString(7, resourceHistory.getResourceData());    
        } else {
            ps.setNull(7, Types.VARCHAR);
        }
        if (resourceHistory.getResourceChecksum() != null) {
            ps.setLong(8, resourceHistory.getResourceChecksum());    
        } else {
            ps.setNull(8, Types.BIGINT);
        }
        ps.setBoolean(9, resourceHistory.isDeleted());
        ps.setString(10, resourceHistory.getExchangeBatchId());
        ps.setString(11, resourceHistory.getVersion());

        return ps;
    }

    private static PreparedStatement createAndPopulateDeleteResourceCurrentPreparedStatement(EntityManager entityManager, RdbmsResourceCurrent resourceCurrent) throws SQLException {

        PreparedStatement ps = createDeleteResourceCurrentPreparedStatement(entityManager);

        ps.setString(1, resourceCurrent.getServiceId());
        ps.setString(2, resourceCurrent.getPatientId());
        ps.setString(3, resourceCurrent.getResourceType());
        ps.setString(4, resourceCurrent.getResourceId());

        return ps;
    }

    private static PreparedStatement createDeleteResourceCurrentPreparedStatement(EntityManager entityManager) throws SQLException {
        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM resource_current"
                + " WHERE service_id = ?"
                + " AND patient_id = ?"
                + " AND resource_type = ?"
                + " AND resource_id = ?";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createAndPopulateDeleteResourceHistoryPreparedStatement(EntityManager entityManager, RdbmsResourceHistory resourceHistory) throws SQLException {

        PreparedStatement ps = createDeleteResourceHistoryPreparedStatement(entityManager);

        ps.setString(1, resourceHistory.getResourceId());
        ps.setString(2, resourceHistory.getResourceType());
        ps.setTimestamp(3, new java.sql.Timestamp(resourceHistory.getCreatedAt().getTime())); //have to use a timestamp otherwise it treats as a date only
        ps.setString(4, resourceHistory.getVersion());

        return ps;
    }

    private static PreparedStatement createDeleteResourceHistoryPreparedStatement(EntityManager entityManager) throws SQLException {
        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM resource_history"
                + " WHERE resource_id = ?"
                + " AND resource_type = ?"
                + " AND created_at = ?"
                + " AND version = ?";

        return connection.prepareStatement(sql);
    }


    /**
     * logical delete, when we want to delete a resource but maintain our audits
     */
    public void delete(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory(resourceEntry);
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent(resourceEntry);

        //we want to insert a "deleted" row into the resource history, so need to clear some fields
        resourceHistory.setDeleted(true);
        resourceHistory.setResourceData(null);
        resourceHistory.setResourceChecksum(null);

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psResourceCurrent = null;
        PreparedStatement psResourceHistory = null;
        //CallableStatement callableStatement = null;

        try {
            entityManager.getTransaction().begin();

            //can't use JPA to delete without first doing a retrieve (I think), so am going to use
            //just a normal prepared statement to delete the data
            //entityManager.remove(resourceCurrent);
            //entityManager.persist(resourceHistory);

            /*callableStatement = createAndPopulateDeleteResourceCallableStatement(entityManager, resourceEntry);
            callableStatement.execute();*/

            psResourceHistory = createAndPopulateInsertResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createAndPopulateDeleteResourceCurrentPreparedStatement(entityManager, resourceCurrent);
            psResourceCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            //deleteResourceStatementCache.returnCallableStatement(entityManager, callableStatement);

            if (psResourceCurrent != null) {
                psResourceCurrent.close();
            }
            if (psResourceHistory != null) {
                psResourceHistory.close();
            }
            entityManager.close();
        }
    }




    /**
     * physical delete, when we want to remove all trace of data from Discovery
     */
    public void hardDelete(ResourceWrapper resourceEntry) throws Exception {

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory(resourceEntry);
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent(resourceEntry);

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psCurrent = null;
        PreparedStatement psHistory = null;
        //CallableStatement callableStatement = null;

        try {
            entityManager.getTransaction().begin();

            //JPA remove doesn't seem to work without retrieving first, so I'm just using a prepared statement
            //entityManager.remove(resourceHistory);
            //entityManager.remove(resourceCurrent);

            /*callableStatement = createAndPopulatePhysicalDeleteCallableStatement(entityManager, resourceEntry);
            callableStatement.execute();*/

            //delete the entry from resource_history
            psHistory = createAndPopulateDeleteResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psHistory.executeUpdate();

            //and either update or delete from resource_current, depending on what's now the most recent entry in resource_history
            String sql = "select c"
                    + " from RdbmsResourceHistory c"
                    + " where c.resourceType = :resource_type"
                    + " and c.resourceId = :resource_id"
                    + " ORDER BY c.createdAt DESC"; //need to explicitly sort so ordered most recent first

            Query query = entityManager.createQuery(sql, RdbmsResourceHistory.class)
                    .setParameter("resource_type", resourceEntry.getResourceType())
                    .setParameter("resource_id", resourceEntry.getResourceId().toString())
                    .setMaxResults(1);

            List<RdbmsResourceHistory> ret = query.getResultList();
            if (ret.size() == 0) {
                //if there's no remaining resource history, delete from resource_current
                psCurrent = createAndPopulateDeleteResourceCurrentPreparedStatement(entityManager, resourceCurrent);

            } else {
                RdbmsResourceHistory latestHistory = (RdbmsResourceHistory)ret.get(0);
                ResourceWrapper wrapper = new ResourceWrapper(latestHistory);

                if (wrapper.isDeleted()) {
                    //if there is history, but the most recent one is deleted, then delete from resource_current
                    psCurrent = createAndPopulateDeleteResourceCurrentPreparedStatement(entityManager, resourceCurrent);

                } else {
                    //if there is history and it's non-deleted, update resource_current to match
                    RdbmsResourceCurrent newCurrent = new RdbmsResourceCurrent(wrapper);
                    psCurrent = createAndPopulateInsertResourceCurrentPreparedStatement(entityManager, newCurrent);
                }
            }

            psCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            //physicalDeleteResourceStatementCache.returnCallableStatement(entityManager, callableStatement);

            if (psCurrent != null) {
                psCurrent.close();
            }
            if (psHistory != null) {
                psHistory.close();
            }
            entityManager.close();
        }
    }



    /**
     * convenience fn to save repetitive code
     */
    public Resource getCurrentVersionAsResource(UUID serviceId, ResourceType resourceType, String resourceIdStr) throws Exception {
        ResourceWrapper resourceHistory = getCurrentVersion(serviceId, resourceType.toString(), UUID.fromString(resourceIdStr));

        if (resourceHistory == null) {
            return null;
        } else {
            return PARSER_POOL.parse(resourceHistory.getResourceData());
        }
    }

    public ResourceWrapper getCurrentVersion(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.resourceType = :resource_type"
                    + " and c.resourceId = :resource_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId.toString());

            RdbmsResourceCurrent result = (RdbmsResourceCurrent)query.getSingleResult();
            return new ResourceWrapper(result);

        } catch (NoResultException ex) {
            return null;

        } catch (NonUniqueResultException nu) {
            //seen this exception a couple of times in AWS which should not happen, so adding additional logging
            LOG.error("More than one result found for resource_current with resource_type " + resourceType + " and resource_id = " + resourceId);
            throw nu;

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourceHistory(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceHistory c"
                    + " where c.resourceType = :resource_type"
                    + " and c.resourceId = :resource_id"
                    + " ORDER BY c.createdAt DESC"; //need to explicitly sort so ordered most recent first

            Query query = entityManager.createQuery(sql, RdbmsResourceHistory.class)
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId.toString());

            List<RdbmsResourceHistory> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.patientId = :patient_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("patient_id", patientId.toString());

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID systemId, UUID patientId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.patientId = :patient_id"
                    + " and c.resourceType = :resource_type";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("patient_id", patientId.toString())
                    .setParameter("resource_type", resourceType);

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByPatientAllSystems(UUID serviceId, UUID patientId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.patientId = :patient_id"
                    + " and c.resourceType = :resource_type";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("patient_id", patientId.toString())
                    .setParameter("resource_type", resourceType);

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType, List<UUID> resourceIds) throws Exception {

        //convert the list of UUIDs to strings
        List<String> resourceIdStrs = resourceIds
                .stream()
                .map(T -> T.toString())
                .collect(Collectors.toList());

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.resourceId IN :resource_ids";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_ids", resourceIdStrs);

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByServiceAllSystems(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception {

        //convert the list of UUIDs to strings
        List<String> resourceIdStrs = resourceIds
                .stream()
                .map(T -> T.toString())
                .collect(Collectors.toList());

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.resourceId IN :resource_ids";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_ids", resourceIdStrs);

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesForBatch(UUID serviceId, UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceHistory c"
                    + " where c.exchangeBatchId = :batch_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceHistory.class)
                    .setParameter("batch_id", batchId.toString());

            List<RdbmsResourceHistory> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    /**
     * getResourcesForBatch(..) returns the resources as they exactly were when the batch was created,
     * so can return an older version than is currently on the DB. This function returns the CURRENT version
     * of each resource that's in the batch.
     */
    @Override
    public List<ResourceWrapper> getCurrentVersionOfResourcesForBatch(UUID serviceId, UUID batchId) throws Exception {

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            //deleted resources are removed from resource_current, so we need a left outer join
            //and have to select enough columns from resource_history to be able to spot the deleted
            //resources and create resource wrappers for them
            String sql = "select h.service_id, h.system_id, h.resource_type, h.resource_id, h.patient_id, "
                    + " c.system_id, c.patient_id, c.resource_data"
                    + " from resource_history h"
                    + " left outer join resource_current c"
                    + " on h.resource_id = c.resource_id"
                    + " and h.resource_type = c.resource_type"
                    + " where h.exchange_batch_id = ?;";

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            ps = connection.prepareStatement(sql);
            ps.setString(1, batchId.toString());

            List<ResourceWrapper> ret = new ArrayList<>();

            //some transforms can end up saving the same resource multiple times in a batch, so we'll have duplicate
            //rows in our resource_history table. Since they all join to the latest data in resource_current table, we
            //don't need to worry about which we keep, so just use this to avoid duplicates
            Set<String> resourcesDone = new HashSet<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int col = 1;
                String historyServiceId = rs.getString(col++);
                String historySystemId = rs.getString(col++);
                String historyResourceType = rs.getString(col++);
                String historyResourceId = rs.getString(col++);
                String historyPatientId = rs.getString(col++);

                //since we're not dealing with any primitive types, we can just use getString(..)
                //and check that the result is null or not, without needing to use wasNull(..)
                String currentSystemId = rs.getString(col++);
                String currentPatientId = rs.getString(col++);
                String currentResourceData = rs.getString(col);

                //skip if we've already done this resource
                String referenceStr = ReferenceHelper.createResourceReference(historyResourceType, historyResourceId);
                if (resourcesDone.contains(referenceStr)) {
                    continue;
                }
                resourcesDone.add(referenceStr);

                //populate the resource wrapper with what we've got, depending on what's null or not.
                //NOTE: the resource wrapper will have the following fields null:
                //UUID version;
                //Date createdAt;
                //String resourceMetadata;
                //Long resourceChecksum;
                //UUID exchangeId;

                ResourceWrapper wrapper = new ResourceWrapper();
                wrapper.setServiceId(UUID.fromString(historyServiceId));
                wrapper.setResourceType(historyResourceType);
                wrapper.setResourceId(UUID.fromString(historyResourceId));
                wrapper.setExchangeBatchId(batchId);

                //if we have no resource data, the resource is deleted, so populate with what we've got from the history table
                if (currentResourceData == null) {
                    wrapper.setDeleted(true);
                    wrapper.setSystemId(UUID.fromString(historySystemId));
                    if (!Strings.isNullOrEmpty(historyPatientId)) {
                        wrapper.setPatientId(UUID.fromString(historyPatientId));
                    }

                } else {
                    //if we have resource data, then populate with what we've got from resource_current
                    wrapper.setSystemId(UUID.fromString(currentSystemId));
                    wrapper.setResourceData(currentResourceData);
                    if (!Strings.isNullOrEmpty(currentPatientId)) {
                        wrapper.setPatientId(UUID.fromString(currentPatientId));
                    }
                }

                ret.add(wrapper);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }


    public Long getResourceChecksum(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c.resourceChecksum"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.resourceType = :resource_type"
                    + " and c.resourceId = :resource_id";

            Query query = entityManager.createQuery(sql)
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId.toString());

            Long ret = null;

            List<Long> list = query.getResultList();

            //a resource should only exist in the table once, so if there are multiple, then something is wrong
            if (list.size() > 1) {
                throw new Exception("Found " + list.size() + " checksums for " + resourceType + " " + resourceId);
            }

            for (Long s : list) {
                ret = s;
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    /**
     * tests if we have any patient cassandra stored for the given service and system
     */
    public boolean dataExists(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setMaxResults(1);

            RdbmsResourceCurrent r = (RdbmsResourceCurrent)query.getSingleResult();
            return true;

        } catch (NoResultException ex) {
            return false;

        } finally {
            entityManager.close();
        }
    }

    public ResourceWrapper getFirstResourceByService(UUID serviceId, UUID systemId, ResourceType resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType = :resource_type"
                    + " ORDER BY c.updatedAt ASC";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType.toString())
                    .setMaxResults(1);

            RdbmsResourceCurrent result = (RdbmsResourceCurrent)query.getSingleResult();
            return new ResourceWrapper(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType = :resource_type";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType.toString());

            List<RdbmsResourceCurrent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ResourceWrapper(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, UUID systemId, String resourceType, Class<T> classOfT) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "SELECT c.resourceMetadata"
                    + " FROM RdbmsResourceCurrent c"
                    + " WHERE c.serviceId = :service_id"
                    + " AND c.resourceType = :resource_type";

            Query q = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType);

            List<String> results = q.getResultList();

            return new ResourceMetadataIterator<>(results.iterator(), classOfT);

        } finally {
            entityManager.close();
        }
    }

    public long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "SELECT COUNT(c)"
                    + " FROM RdbmsResourceCurrent c"
                    + " WHERE c.serviceId = :service_id"
                    + " AND c.resourceType = :resource_type";

            Query q = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType);

            return (long)q.getSingleResult();

        } finally {
            entityManager.close();
        }
    }
}
