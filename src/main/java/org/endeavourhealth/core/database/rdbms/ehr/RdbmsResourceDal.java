package org.endeavourhealth.core.database.rdbms.ehr;

import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ParserPool;
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
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsResourceDal implements ResourceDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceDal.class);
    private static final ParserPool PARSER_POOL = new ParserPool();

    public void save(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory(resourceEntry);
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent(resourceEntry);

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        PreparedStatement psResourceCurrent = null;
        PreparedStatement psResourceHistory = null;

        try {
            entityManager.getTransaction().begin();
            
            //we want to do an INSERT ... ON DUPLICATE KEY UPDATE, which isn't possible through JPA, so
            //I'm just dropping to using a prepared statement to save it, which means we don't need to perform
            //a read before each write as it's all handled in MySQL
            //entityManager.persist(resourceCurrent);
            //entityManager.persist(resourceHistory);
            
            psResourceHistory = createAndPopulateInsertResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createAndPopulateInsertResourceCurrentPreparedStatement(entityManager, resourceCurrent);
            psResourceCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (psResourceCurrent != null) {
                psResourceCurrent.close();
            }
            if (psResourceHistory != null) {
                psResourceHistory.close();
            }
        }
    }

    private static PreparedStatement createInsertResourceCurrentPreparedStatement(EntityManager entityManager) throws Exception {

        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "INSERT INTO resource_current"
                + " (service_id, system_id, resource_type, resource_id, updated_at, patient_id, resource_data, resource_checksum, resource_metadata)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE"
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

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        PreparedStatement psResourceCurrent = null;
        PreparedStatement psResourceHistory = null;

        try {
            entityManager.getTransaction().begin();

            //can't use JPA to delete without first doing a retrieve (I think), so am going to use
            //just a normal prepared statement to delete the data
            //entityManager.remove(resourceCurrent);
            //entityManager.persist(resourceHistory);

            psResourceHistory = createAndPopulateInsertResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createAndPopulateDeleteResourceCurrentPreparedStatement(entityManager, resourceCurrent);
            psResourceCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (psResourceCurrent != null) {
                psResourceCurrent.close();
            }
            if (psResourceHistory != null) {
                psResourceHistory.close();
            }
        }
    }

    private static PreparedStatement createAndPopulateDeleteResourceCurrentPreparedStatement(EntityManager entityManager, RdbmsResourceCurrent resourceCurrent) throws SQLException {

        PreparedStatement ps = createDeleteResourceCurrentPreparedStatement(entityManager);

        ps.setString(1, resourceCurrent.getServiceId());
        ps.setString(2, resourceCurrent.getSystemId());
        ps.setString(3, resourceCurrent.getPatientId());
        ps.setString(4, resourceCurrent.getResourceType());
        ps.setString(5, resourceCurrent.getResourceId());

        return ps;
    }

    private static PreparedStatement createDeleteResourceCurrentPreparedStatement(EntityManager entityManager) throws SQLException {
        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM resource_current"
                + " WHERE service_id = ?"
                + " AND system_id = ?"
                + " AND patient_id = ?"
                + " AND resource_type = ?"
                + " AND resource_id = ?";

        return connection.prepareStatement(sql);
    }

    private static PreparedStatement createAndPopulateDeleteResourceHistoryPreparedStatement(EntityManager entityManager, RdbmsResourceHistory resourceHistory) throws SQLException {

        PreparedStatement ps = createDeleteResourceHistoryPreparedStatement(entityManager);

        ps.setString(1, resourceHistory.getResourceId());
        ps.setString(2, resourceHistory.getResourceType());
        //have to use the timestamp function otherwise it treats as a date only
        //ps.setDate(3, new java.sql.Date(resourceHistory.getCreatedAt().getTime()));
        ps.setTimestamp(3, new java.sql.Timestamp(resourceHistory.getCreatedAt().getTime()));

        return ps;
    }

    private static PreparedStatement createDeleteResourceHistoryPreparedStatement(EntityManager entityManager) throws SQLException {
        SessionImpl session = (SessionImpl)entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM resource_history"
                + " WHERE resource_id = ?"
                + " AND resource_type = ?"
                + " AND created_at = ?";

        return connection.prepareStatement(sql);
    }

    /**
     * physical delete, when we want to remove all trace of cassandra from Discovery
     */
    public void hardDelete(ResourceWrapper keys) throws Exception {

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory();
        resourceHistory.setServiceId(keys.getServiceId().toString());
        resourceHistory.setSystemId(keys.getSystemId().toString());
        resourceHistory.setResourceType(keys.getResourceType());
        resourceHistory.setResourceId(keys.getResourceId().toString());
        resourceHistory.setCreatedAt(keys.getCreatedAt());
        resourceHistory.setDeleted(true);
        resourceHistory.setResourceData(null);
        resourceHistory.setResourceChecksum(null);
        resourceHistory.setExchangeBatchId(keys.getExchangeBatchId().toString());
        resourceHistory.setVersion(keys.getVersion().toString());

        //we're going to DELETE from the resource_current table, so really only need
        //to populate the primary key columns, but I'm doing all of them for consistency with the above method
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent();
        resourceCurrent.setServiceId(keys.getServiceId().toString());
        resourceCurrent.setSystemId(keys.getSystemId().toString());
        resourceCurrent.setResourceType(keys.getResourceType());
        resourceCurrent.setResourceId(keys.getResourceId().toString());
        resourceCurrent.setUpdatedAt(keys.getCreatedAt());
        resourceCurrent.setResourceData(null);
        resourceCurrent.setResourceChecksum(keys.getResourceChecksum());
        resourceCurrent.setResourceMetadata(keys.getResourceMetadata());

        if (keys.getPatientId() != null) {
            resourceHistory.setPatientId(keys.getPatientId().toString());
            resourceCurrent.setPatientId(keys.getPatientId().toString());
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();
        PreparedStatement psCurrent = null;
        PreparedStatement psHistory = null;

        try {
            entityManager.getTransaction().begin();

            //JPA remove doesn't seem to work without retrieving first, so I'm just using a prepared statement
            //entityManager.remove(resourceHistory);
            //entityManager.remove(resourceCurrent);

            psCurrent = createAndPopulateDeleteResourceCurrentPreparedStatement(entityManager, resourceCurrent);
            psCurrent.executeUpdate();

            psHistory = createAndPopulateDeleteResourceHistoryPreparedStatement(entityManager, resourceHistory);
            psHistory.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();

            if (psCurrent != null) {
                psCurrent.close();
            }
            if (psHistory != null) {
                psHistory.close();
            }
        }
    }

    /**
     * convenience fn to save repetitive code
     */
    public Resource getCurrentVersionAsResource(ResourceType resourceType, String resourceIdStr) throws Exception {
        ResourceWrapper resourceHistory = getCurrentVersion(resourceType.toString(), UUID.fromString(resourceIdStr));

        if (resourceHistory == null) {
            return null;
        } else {
            return PARSER_POOL.parse(resourceHistory.getResourceData());
        }
    }

    public ResourceWrapper getCurrentVersion(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourceHistory(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.patientId = :patient_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.patientId = :patient_id"
                    + " and c.resourceType = :resource_type";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.resourceId IN :resource_ids";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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

    public List<ResourceWrapper> getResourcesForBatch(UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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


    public Long getResourceChecksum(String resourceType, UUID resourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.resourceType = :resource_type"
                    + " ORDER BY c.updatedAt ASC";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.resourceType = :resource_type";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
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
        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "SELECT c.resourceMetadata"
                    + " FROM RdbmsResourceCurrent c"
                    + " WHERE c.serviceId = :service_id"
                    + " AND c.systemId = :system_id"
                    + " AND c.resourceType = :resource_type";

            Query q = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setParameter("resource_type", resourceType);

            List<String> results = q.getResultList();

            return new ResourceMetadataIterator<>(results.iterator(), classOfT);

        } finally {
            entityManager.close();
        }
    }

    public long getResourceCountByService(UUID serviceId, UUID systemId, String resourceType) throws Exception {

        EntityManager entityManager = ConnectionManager.getEhrEntityManager();

        try {
            String sql = "SELECT COUNT(c)"
                    + " FROM RdbmsResourceCurrent c"
                    + " WHERE c.serviceId = :service_id"
                    + " AND c.systemId = :system_id"
                    + " AND c.resourceType = :resource_type";

            Query q = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setParameter("resource_type", resourceType);

            return (long)q.getSingleResult();

        } finally {
            entityManager.close();
        }
    }
}
