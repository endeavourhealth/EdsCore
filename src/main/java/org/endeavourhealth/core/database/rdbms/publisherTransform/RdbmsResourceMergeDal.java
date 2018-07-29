package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.ResourceMergeDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceMergeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceMergeMap;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsResourceMergeDal implements ResourceMergeDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsResourceMergeDal.class);

    @Override
    public ResourceMergeMap readMergeRecord(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        RdbmsResourceMergeMap r = readMergeRecordDB(serviceId, resourceType, resourceId);
        if (r == null) {
            return null;
        } else {
            return new ResourceMergeMap(r);
        }
    }

    private RdbmsResourceMergeMap readMergeRecordDB(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        //LOG.trace("readMergeRecordDB:" + resourceType + " " + resourceId);
        UUID ret = null;
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceMergeMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType LIKE :resource_type"
                    + " and c.sourceResourceId LIKE :resource_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceMergeMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId.toString())
                    .setMaxResults(1);

            try {
                return (RdbmsResourceMergeMap) query.getSingleResult();
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void insertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception {
        //LOG.trace("insertMergeRecord:" + resourceType + " " + resourceFrom);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            SessionImpl session = (SessionImpl)entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO resource_merge_map"
                    + " (service_id, resource_type, source_resource_id, destination_resource_id, updated_at)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " destination_resource_id = VALUES(destination_resource_id),"
                    + " updated_at = VALUES(updated_at);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, serviceId.toString());
            ps.setString(2, resourceType);
            ps.setString(3, resourceFrom.toString());
            ps.setString(4, resourceTo.toString());
            ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));

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
    /*@Override
    public void insertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception {
        LOG.trace("insertMergeRecord:" + resourceType + " " + resourceFrom);
        RdbmsResourceMergeMap dbObj = new RdbmsResourceMergeMap();
        dbObj.setServiceId(serviceId.toString());
        dbObj.setResourceType(resourceType);
        dbObj.setSourceResourceId(resourceFrom.toString());
        dbObj.setDestinationResourceId(resourceTo.toString());
        dbObj.setUpdatedAt(new Date());

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            //LOG.trace("About to save " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();
            //LOG.trace("Saved mergeRecord for " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }*/

    @Override
    public void updateMergeRecord(RdbmsResourceMergeMap dbObj) throws Exception {
        //LOG.trace("updateMergeRecord");
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(UUID.fromString(dbObj.getServiceId()));

        try {
            //LOG.trace("About to save " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());
            entityManager.getTransaction().begin();
            dbObj.setUpdatedAt(new Date());
            entityManager.refresh(dbObj);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void upsertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception {
        //LOG.trace("upsertMergeRecord:" + resourceType + " " + resourceFrom);
        UUID ret = null;
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceMergeMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType LIKE :resource_type"
                    + " and c.sourceResourceId LIKE :resource_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceMergeMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceFrom.toString())
                    .setMaxResults(1);

            try {
                RdbmsResourceMergeMap r = (RdbmsResourceMergeMap) query.getSingleResult();
                r.setDestinationResourceId(resourceTo.toString());
                entityManager.getTransaction().begin();
                r.setUpdatedAt(new Date());
                entityManager.refresh(r);
                entityManager.getTransaction().commit();
            }
            catch (NoResultException e) {
                entityManager.close();
                insertMergeRecord(serviceId, resourceType, resourceFrom, resourceTo);
            }
            catch (Exception ex) {
                entityManager.getTransaction().rollback();
                throw ex;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public UUID resolveMergeUUID(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        //LOG.trace("resolveMergeUUID:" + resourceType + " " + resourceId);
        UUID ret = resourceId;
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceMergeMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType LIKE :resource_type"
                    + " and c.sourceResourceId LIKE :resource_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceMergeMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId.toString())
                    .setMaxResults(1);

            try {
                RdbmsResourceMergeMap mergeRecord = (RdbmsResourceMergeMap) query.getSingleResult();

                // There may be a chain of linked resources
                while (true) {
                    ret = UUID.fromString(mergeRecord.getDestinationResourceId());

                    query = entityManager.createQuery(sql, RdbmsResourceMergeMap.class)
                            .setParameter("service_id", serviceId.toString())
                            .setParameter("resource_type", resourceType)
                            .setParameter("resource_id", ret.toString())
                            .setMaxResults(1);

                    mergeRecord = (RdbmsResourceMergeMap) query.getSingleResult();
                }

            }
            catch (NoResultException e) {
                return ret;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public String resolveMerge(String serviceId, String resourceType, String resourceId) throws Exception {
        return resolveMergeUUID(UUID.fromString(serviceId), resourceType, UUID.fromString(resourceId)).toString();
    }

    @Override
    public List<ResourceMergeMap> retrieveMergeMappings(UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceMergeMap c"
                    + " where c.serviceId = :service_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceMergeMap.class)
                    .setParameter("service_id", serviceId.toString());

            List<RdbmsResourceMergeMap> results = query.getResultList();
            return results
                    .stream()
                    .map(T -> new ResourceMergeMap((T)))
                    .collect(Collectors.toList());

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
