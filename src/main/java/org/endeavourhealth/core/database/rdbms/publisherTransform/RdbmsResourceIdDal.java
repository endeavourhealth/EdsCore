package org.endeavourhealth.core.database.rdbms.publisherTransform;


import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceIdMap;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class RdbmsResourceIdDal implements ResourceIdTransformDalI {

    public void insert(ResourceIdMap resourceIdMap) throws Exception {
        if (resourceIdMap == null) {
            throw new IllegalArgumentException("resourceIdMap is null");
        }

        RdbmsResourceIdMap dbObj = new RdbmsResourceIdMap(resourceIdMap);

        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.resourceType = :resource_type"
                    + " and c.sourceId = :source_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("source_id", sourceId.toString());

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return new ResourceIdMap(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception {
        return getResourceIdMapByEdsId(resourceType, UUID.fromString(edsId));
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) throws Exception {
        EntityManager entityManager = ConnectionManager.getPublisherTransformEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsResourceIdMap c"
                    + " where c.resourceType = :resource_type"
                    + " and c.edsId = :eds_id";

            Query query = entityManager.createQuery(sql, RdbmsResourceIdMap.class)
                    .setParameter("resource_type", resourceType.toString())
                    .setParameter("eds_id", edsId.toString());

            RdbmsResourceIdMap result = (RdbmsResourceIdMap)query.getSingleResult();
            return new ResourceIdMap(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

}
