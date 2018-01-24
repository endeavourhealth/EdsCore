package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.ResourceMergeDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceMergeMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceMergeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

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
        LOG.trace("readMergeRecordDB:" + resourceType + " " + resourceId);
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
    }

    @Override
    public void updateMergeRecord(RdbmsResourceMergeMap dbObj) throws Exception {
        LOG.trace("updateMergeRecord");
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(UUID.fromString(dbObj.getServiceId()));

        try {
            //LOG.trace("About to save " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());
            entityManager.getTransaction().begin();
            dbObj.setUpdatedAt(new Date());
            entityManager.refresh(dbObj);
            entityManager.getTransaction().commit();
            //LOG.trace("Saved mergeRecord for " + resourceType + " " + resourceFrom.toString() + "==>" + resourceTo.toString());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void upsertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception {
        LOG.trace("upsertMergeRecord:" + resourceType + " " + resourceFrom);
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
        LOG.trace("resolveMergeUUID:" + resourceType + " " + resourceId);
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
                            .setParameter("service_id", serviceId)
                            .setParameter("resource_type", resourceType)
                            .setParameter("resource_id", ret)
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
}
