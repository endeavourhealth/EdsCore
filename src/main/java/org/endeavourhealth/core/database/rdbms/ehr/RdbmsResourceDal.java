package org.endeavourhealth.core.database.rdbms.ehr;

import com.google.common.base.Strings;
import org.endeavourhealth.common.cache.ParserPool;
import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.dal.ehr.ResourceDalI;
import org.endeavourhealth.core.database.dal.ehr.ResourceMetadataIterator;
import org.endeavourhealth.core.database.dal.ehr.models.ResourceWrapper;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.DeadlockHandler;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceCurrent;
import org.endeavourhealth.core.database.rdbms.ehr.models.RdbmsResourceHistory;
import org.endeavourhealth.core.exceptions.TransformException;
import org.endeavourhealth.core.fhirStorage.metadata.ResourceMetadata;
import org.hibernate.internal.SessionImpl;
import org.hl7.fhir.instance.model.Reference;
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
import java.util.Date;
import java.util.stream.Collectors;

public class RdbmsResourceDal implements ResourceDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceDal.class);

    private static final ParserPool PARSER_POOL = new ParserPool();



    @Override
    public void save(List<ResourceWrapper> wrappers) throws Exception {
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

    private UUID findServiceId(List<ResourceWrapper> wrappers) throws Exception {

        if (wrappers == null || wrappers.isEmpty()) {
            throw new IllegalArgumentException("trying to save null or empty resources");
        }

        UUID serviceId = null;
        for (ResourceWrapper wrapper : wrappers) {
            if (serviceId == null) {
                serviceId = wrapper.getServiceId();
            } else if (!serviceId.equals(wrapper.getServiceId())) {
                throw new IllegalArgumentException("Can't save resources for different services");
            }
        }
        return serviceId;
    }

    private void trySave(List<ResourceWrapper> wrappers) throws Exception {

        UUID serviceId = findServiceId(wrappers);
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psResourceHistory = null;
        PreparedStatement psResourceCurrent = null;

        try {
            entityManager.getTransaction().begin();

            psResourceHistory = createInsertResourceHistoryPreparedStatement(entityManager);
            for (ResourceWrapper wrapper : wrappers) {
                populateInsertResourceHistoryPreparedStatement(wrapper, psResourceHistory);
                psResourceHistory.addBatch();
            }
            psResourceHistory.executeBatch();

            psResourceCurrent = createInsertResourceCurrentPreparedStatement(entityManager);
            for (ResourceWrapper wrapper : wrappers) {
                populateInsertResourceCurrentPreparedStatement(wrapper, psResourceCurrent);
                psResourceCurrent.addBatch();
            }
            psResourceCurrent.executeBatch();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psResourceCurrent != null) {
                psResourceCurrent.close();
            }
            if (psResourceHistory != null) {
                psResourceHistory.close();
            }
            entityManager.close();
        }
    }

    @Override
    public void delete(List<ResourceWrapper> wrappers) throws Exception {
        //allow several attempts if it fails due to a deadlock
        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                tryDelete(wrappers);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }

    }

    public void tryDelete(List<ResourceWrapper> wrappers) throws Exception {

        UUID serviceId = findServiceId(wrappers);

        //we want to insert a "deleted" row into the resource history, so need to clear some fields
        for (ResourceWrapper wrapper : wrappers) {
            wrapper.setDeleted(true);
            wrapper.setResourceData(null);
            wrapper.setResourceChecksum(null);
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psResourceCurrent = null;
        PreparedStatement psResourceHistory = null;

        try {
            entityManager.getTransaction().begin();

            psResourceHistory = createInsertResourceHistoryPreparedStatement(entityManager);
            for (ResourceWrapper wrapper : wrappers) {
                populateInsertResourceHistoryPreparedStatement(wrapper, psResourceHistory);
                psResourceHistory.addBatch();
            }
            psResourceHistory.executeBatch();

            psResourceCurrent = createDeleteResourceCurrentPreparedStatement(entityManager);
            for (ResourceWrapper wrapper : wrappers) {
                populateDeleteResourceCurrentPreparedStatement(wrapper, psResourceCurrent);
                psResourceCurrent.addBatch();
            }
            psResourceCurrent.executeBatch();

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


    public void save(ResourceWrapper resourceEntry) throws Exception {

        //attempts the save, and if the save fails because of a deadlock, it will have a second attempt
        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                trySave(resourceEntry);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    public void trySave(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        //CallableStatement storedProcedure = null;
        PreparedStatement psResourceHistory = null;
        PreparedStatement psResourceCurrent = null;

        try {
            entityManager.getTransaction().begin();

            psResourceHistory = createInsertResourceHistoryPreparedStatement(entityManager);
            populateInsertResourceHistoryPreparedStatement(resourceEntry, psResourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createInsertResourceCurrentPreparedStatement(entityManager);
            populateInsertResourceCurrentPreparedStatement(resourceEntry, psResourceCurrent);
            psResourceCurrent.executeUpdate();

            entityManager.getTransaction().commit();
            // Try to handle what is probably a MySql driver bug. And yes I know catch with class name is bad.
            // But this seems to be a quite specific bug in this one class and we don't want to miss other NPEs
            // that might get introduced.
        } catch (NullPointerException npe) {
            if (npe.getClass().getName().equals("ServerPreparedQueryBindValue")) {
                throw new TransformException("Nullpointer in ServerPreparedQueryBindValue. MySql bug?");
            } else {
                entityManager.getTransaction().rollback();
                throw npe;
            }

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
                + " resource_metadata = VALUES(resource_metadata)";

        return connection.prepareStatement(sql);
    }

    private static void populateInsertResourceCurrentPreparedStatement(ResourceWrapper wrapper, PreparedStatement ps) throws Exception {

        ps.setString(1, wrapper.getServiceId().toString());
        ps.setString(2, wrapper.getSystemId().toString());
        ps.setString(3, wrapper.getResourceType());
        ps.setString(4, wrapper.getResourceId().toString());
        ps.setTimestamp(5, new java.sql.Timestamp(wrapper.getCreatedAt().getTime()));
        if (wrapper.getPatientId() != null) {
            ps.setString(6, wrapper.getPatientId().toString());
        } else {
            //the patient_id column doesn't allow nulls, as it's part of an index, so insert empty String instead
            ps.setString(6, "");
            //ps.setNull(6, Types.VARCHAR);
        }
        ps.setString(7, wrapper.getResourceData());
        ps.setLong(8, wrapper.getResourceChecksum());
        ps.setString(9, wrapper.getResourceMetadata());
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

    private static void populateInsertResourceHistoryPreparedStatement(ResourceWrapper wrapper, PreparedStatement ps) throws Exception {

        ps.setString(1, wrapper.getServiceId().toString());
        ps.setString(2, wrapper.getSystemId().toString());
        ps.setString(3, wrapper.getResourceType());
        ps.setString(4, wrapper.getResourceId().toString());
        ps.setTimestamp(5, new java.sql.Timestamp(wrapper.getCreatedAt().getTime()));
        if (wrapper.getPatientId() != null) {
            ps.setString(6, wrapper.getPatientId().toString());
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        if (!Strings.isNullOrEmpty(wrapper.getResourceData())) {
            ps.setString(7, wrapper.getResourceData());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }
        if (wrapper.getResourceChecksum() != null) {
            ps.setLong(8, wrapper.getResourceChecksum());
        } else {
            ps.setNull(8, Types.BIGINT);
        }
        ps.setBoolean(9, wrapper.isDeleted());
        ps.setString(10, wrapper.getExchangeBatchId().toString());
        ps.setString(11, wrapper.getVersion().toString());
    }

    private static void populateDeleteResourceCurrentPreparedStatement(ResourceWrapper wrapper, PreparedStatement ps) throws SQLException {

        int col = 1;
        ps.setString(col++, wrapper.getServiceId().toString());
        if (wrapper.getPatientId() != null) {
            ps.setString(col++, wrapper.getPatientId().toString());
        } else {
            ps.setString(col++, ""); //DB field doesn't allow nulls so save as empty String
        }
        ps.setString(col++, wrapper.getResourceType());
        ps.setString(col++, wrapper.getResourceId().toString());
    }

    private static PreparedStatement createDeleteResourceCurrentPreparedStatement(EntityManager entityManager) throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();

        String sql = "DELETE FROM resource_current"
                + " WHERE service_id = ?"
                + " AND patient_id = ?"
                + " AND resource_type = ?"
                + " AND resource_id = ?";

        return connection.prepareStatement(sql);
    }


    /**
     * logical delete, when we want to delete a resource but maintain our audits
     */
    @Override
    public void delete(ResourceWrapper resourceEntry) throws Exception {

        //attempts the save, and if the save fails because of a deadlock, it will have a second attempt
        DeadlockHandler h = new DeadlockHandler();
        while (true) {
            try {
                tryDelete(resourceEntry);
                break;

            } catch (Exception ex) {
                h.handleError(ex);
            }
        }
    }

    public void tryDelete(ResourceWrapper resourceEntry) throws Exception {
        if (resourceEntry == null) {
            throw new IllegalArgumentException("resourceEntry is null");
        }

        //we want to insert a "deleted" row into the resource history, so need to clear some fields
        resourceEntry.setDeleted(true);
        resourceEntry.setResourceData(null);
        resourceEntry.setResourceChecksum(null);

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

            psResourceHistory = createInsertResourceHistoryPreparedStatement(entityManager);
            populateInsertResourceHistoryPreparedStatement(resourceEntry, psResourceHistory);
            psResourceHistory.executeUpdate();

            psResourceCurrent = createDeleteResourceCurrentPreparedStatement(entityManager);
            populateDeleteResourceCurrentPreparedStatement(resourceEntry, psResourceCurrent);
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
    @Override
    public void hardDeleteResourceAndAllHistory(ResourceWrapper resourceEntry) throws Exception {

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psCurrent = null;
        PreparedStatement psHistory = null;

        try {
            entityManager.getTransaction().begin();

            //delete ALL entries from resource_history for this resource
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM resource_history"
                    + " WHERE resource_id = ?"
                    + " AND resource_type = ?";

            psHistory = connection.prepareStatement(sql);

            int col = 1;
            psHistory.setString(col++, resourceEntry.getResourceId().toString());
            psHistory.setString(col++, resourceEntry.getResourceType());
            psHistory.executeUpdate();

            //we also delete from resource current at other times, so call the same fn for them
            psCurrent = createDeleteResourceCurrentPreparedStatement(entityManager);
            populateDeleteResourceCurrentPreparedStatement(resourceEntry, psCurrent);
            psCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psCurrent != null) {
                psCurrent.close();
            }
            if (psHistory != null) {
                psHistory.close();
            }
            entityManager.close();
        }
    }




    /*public void hardDelete(ResourceWrapper resourceEntry) throws Exception {

        RdbmsResourceHistory resourceHistory = new RdbmsResourceHistory(resourceEntry);
        RdbmsResourceCurrent resourceCurrent = new RdbmsResourceCurrent(resourceEntry);

        UUID serviceId = resourceEntry.getServiceId();
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement psCurrent = null;
        PreparedStatement psHistory = null;
        //CallableStatement callableStatement = null;

        try {
            entityManager.getTransaction().begin();

            //delete the entry from resource_history
            psHistory = createDeleteResourceHistoryPreparedStatement(entityManager);
            populateDeleteResourceHistoryPreparedStatement(resourceEntry, psHistory);
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
                psCurrent = createDeleteResourceCurrentPreparedStatement(entityManager);
                populateDeleteResourceCurrentPreparedStatement(resourceEntry, psCurrent);

            } else {
                RdbmsResourceHistory latestHistory = (RdbmsResourceHistory) ret.get(0);
                ResourceWrapper wrapper = new ResourceWrapper(latestHistory);

                if (wrapper.isDeleted()) {
                    //if there is history, but the most recent one is deleted, then delete from resource_current
                    psCurrent = createDeleteResourceCurrentPreparedStatement(entityManager);
                    populateDeleteResourceCurrentPreparedStatement(resourceEntry, psCurrent);

                } else {
                    //if there is history and it's non-deleted, update resource_current to match
                    psCurrent = createInsertResourceCurrentPreparedStatement(entityManager);
                    populateInsertResourceCurrentPreparedStatement(wrapper, psCurrent);
                }
            }

            psCurrent.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (psCurrent != null) {
                psCurrent.close();
            }
            if (psHistory != null) {
                psHistory.close();
            }
            entityManager.close();
        }
    }*/


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

            RdbmsResourceCurrent result = (RdbmsResourceCurrent) query.getSingleResult();
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

    @Override
    public Map<String, ResourceWrapper> getCurrentVersionForReferences(UUID serviceId, List<String> references) throws Exception {

        if (references.isEmpty()) {
            return new HashMap<>();
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select service_id, system_id, resource_type, resource_id, updated_at, patient_id, resource_data, resource_checksum"
                    + " from"
                    + " resource_current"
                    + " where (";
            for (int i=0; i<references.size(); i++) {
                if (i>0) {
                    sql += ") or (";
                }
                sql += "resource_type = ? and resource_id = ?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;

            for (String referenceStr: references) {
                Reference reference = ReferenceHelper.createReference(referenceStr);
                ReferenceComponents comps = ReferenceHelper.getReferenceComponents(reference);

                ps.setString(col++, comps.getResourceType().toString());
                ps.setString(col++, comps.getId());
            }

            Map<String, ResourceWrapper> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String serviceIdStr = rs.getString(col++);
                String systemIdStr = rs.getString(col++);
                String type = rs.getString(col++);
                String id = rs.getString(col++);
                java.sql.Timestamp tsUpdatedAt = rs.getTimestamp(col++);
                String patientIdStr = rs.getString(col++);
                String resourceJson = rs.getString(col++);
                long resourceChecksum = rs.getLong(col++);

                ResourceWrapper wrapper = new ResourceWrapper();
                wrapper.setServiceId(UUID.fromString(serviceIdStr));
                wrapper.setSystemId(UUID.fromString(systemIdStr));
                wrapper.setResourceType(type);
                wrapper.setResourceId(UUID.fromString(id));
                wrapper.setCreatedAt(new java.util.Date(tsUpdatedAt.getTime()));
                if (!Strings.isNullOrEmpty(patientIdStr)) {
                    wrapper.setPatientId(UUID.fromString(patientIdStr));
                }
                wrapper.setResourceData(resourceJson);
                wrapper.setResourceChecksum(new Long(resourceChecksum));

                String reference = ReferenceHelper.createResourceReference(type, id);
                ret.put(reference, wrapper);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
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

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);
        PreparedStatement ps = null;
        try {
            String sql = "select system_id, resource_type, resource_id, updated_at, resource_data, resource_checksum"
                    + " from resource_current"
                    + " where service_id = ?"
                    + " and patient_id = ?";

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            ps = connection.prepareStatement(sql);

            int col = 1;
            ps.setString(col++, serviceId.toString());
            if (patientId != null) {
                ps.setString(col++, patientId.toString());
            } else {
                //for admin resourecs, the patient ID will be a non-null empty string
                ps.setString(col++, "");
            }

            List<ResourceWrapper> ret = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                col = 1;

                ResourceWrapper w = new ResourceWrapper();
                w.setServiceId(serviceId);
                w.setPatientId(patientId);
                w.setSystemId(UUID.fromString(rs.getString(col++)));
                w.setResourceType(rs.getString(col++));
                w.setResourceId(UUID.fromString(rs.getString(col++)));
                w.setCreatedAt(new Date(rs.getTimestamp(col++).getTime()));
                w.setResourceData(rs.getString(col++));
                w.setResourceChecksum(new Long(rs.getLong(col++)));

                ret.add(w);
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

    }

    public List<ResourceWrapper> getResourcesByPatient(UUID serviceId, UUID patientId, String resourceType) throws Exception {
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

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType, List<UUID> resourceIds) throws Exception {

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
            //changed the order of columns so that we never get a null string in the last column,
            //which exposed a bug in the MySQL driver (https://bugs.mysql.com/bug.php?id=84084)
            String sql = "select h.service_id, h.system_id, h.patient_id, "
                    + " c.system_id, c.updated_at, c.patient_id, c.resource_data, h.resource_type, h.resource_id"
                    + " from resource_history h"
                    + " left outer join resource_current c"
                    + " on h.resource_id = c.resource_id"
                    + " and h.resource_type = c.resource_type"
                    + " where h.exchange_batch_id = ?";

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
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
                /*String historyResourceType = rs.getString(col++);
                String historyResourceId = rs.getString(col++);*/
                String historyPatientId = rs.getString(col++);

                //since we're not dealing with any primitive types, we can just use getString(..)
                //and check that the result is null or not, without needing to use wasNull(..)
                String currentSystemId = rs.getString(col++);
                Date currentUpdatedAt = null;
                java.sql.Timestamp ts = rs.getTimestamp(col++);
                if (ts != null) {
                    currentUpdatedAt = new Date(ts.getTime());
                }
                String currentPatientId = rs.getString(col++);
                String currentResourceData = rs.getString(col++);

                //moved these columns to be the last in the result set, to avoid MySQL bug https://bugs.mysql.com/bug.php?id=84084
                String historyResourceType = rs.getString(col++);
                String historyResourceId = rs.getString(col++);

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
                    wrapper.setCreatedAt(currentUpdatedAt);
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

    @Override
    public Map<String, Long> getResourceChecksumsForReferences(UUID serviceId, List<String> references) throws Exception {

        if (references.isEmpty()) {
            return new HashMap<>();
        }

        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "select resource_type, resource_id, resource_checksum"
                    + " from"
                    + " resource_current"
                    + " where (";
            for (int i=0; i<references.size(); i++) {
                if (i>0) {
                    sql += ") or (";
                }
                sql += "resource_type = ? and resource_id = ?";
            }
            sql += ")";

            ps = connection.prepareStatement(sql);

            int col = 1;

            for (String referenceStr: references) {
                Reference reference = ReferenceHelper.createReference(referenceStr);
                ReferenceComponents comps = ReferenceHelper.getReferenceComponents(reference);

                ps.setString(col++, comps.getResourceType().toString());
                ps.setString(col++, comps.getId());
            }

            Map<String, Long> ret = new HashMap<>();

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                col = 1;
                String type = rs.getString(col++);
                String id = rs.getString(col++);
                long checksum = rs.getLong(col++);

                String reference = ReferenceHelper.createResourceReference(type, id);
                ret.put(reference, new Long(checksum));
            }

            return ret;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }

    }

    /**
     * tests if we have any patient cassandra stored for the given service and system
     */
    public boolean dataExists(UUID serviceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceCurrent c"
                    + " where c.serviceId = :service_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceCurrent.class)
                    .setParameter("service_id", serviceId.toString())
                    .setMaxResults(1);

            RdbmsResourceCurrent r = (RdbmsResourceCurrent) query.getSingleResult();
            return true;

        } catch (NoResultException ex) {
            return false;

        } finally {
            entityManager.close();
        }
    }

    public ResourceWrapper getFirstResourceByService(UUID serviceId, ResourceType resourceType) throws Exception {
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

            RdbmsResourceCurrent result = (RdbmsResourceCurrent) query.getSingleResult();
            return new ResourceWrapper(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public List<ResourceWrapper> getResourcesByService(UUID serviceId, String resourceType) throws Exception {
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

    public <T extends ResourceMetadata> ResourceMetadataIterator<T> getMetadataByService(UUID serviceId, String resourceType, Class<T> classOfT) throws Exception {
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

    public long getResourceCountByService(UUID serviceId, String resourceType) throws Exception {
        EntityManager entityManager = ConnectionManager.getEhrEntityManager(serviceId);

        try {
            String sql = "SELECT COUNT(c)"
                    + " FROM RdbmsResourceCurrent c"
                    + " WHERE c.serviceId = :service_id"
                    + " AND c.resourceType = :resource_type";

            Query q = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType);

            return (long) q.getSingleResult();

        } finally {
            entityManager.close();
        }
    }
}
