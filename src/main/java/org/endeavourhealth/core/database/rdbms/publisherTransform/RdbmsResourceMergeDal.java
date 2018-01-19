package org.endeavourhealth.core.database.rdbms.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.ResourceMergeDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.jdbcreader.models.RdbmsBatch;
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
    public void recordMerge(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception {
        RdbmsResourceMergeMap dbObj = new RdbmsResourceMergeMap();
        dbObj.setServiceId(serviceId.toString());
        dbObj.setResourceType(resourceType);
        dbObj.setSourceResourceId(resourceFrom.toString());
        dbObj.setDestinationResourceId(resourceTo.toString());
        dbObj.setUpdatedAt(new Date());

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();
            LOG.trace("Saved mergeRecord:" + resourceFrom.toString() + "==>" + resourceTo.toString());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public UUID ResolveMergeUUID(UUID serviceId, String resourceType, UUID resourceId) throws Exception {
        UUID ret = null;
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager(serviceId);
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceMergeMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.resourceType LIKE :resource_type"
                    + " and c.sourceResourceId LIKE :resource_id";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("service_id", serviceId)
                    .setParameter("resource_type", resourceType)
                    .setParameter("resource_id", resourceId)
                    .setMaxResults(1);

            try {
                RdbmsResourceMergeMap mergeRecord = (RdbmsResourceMergeMap) query.getSingleResult();

                // There may be a chain of linked resources
                while (true) {
                    ret = UUID.fromString(mergeRecord.getDestinationResourceId());

                    query = entityManager.createQuery(sql, RdbmsBatch.class)
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
