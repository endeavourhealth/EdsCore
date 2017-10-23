package org.endeavourhealth.core.rdbms.transform;


import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Service;
import org.endeavourhealth.core.rdbms.transform.models.ResourceIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.UUID;

public class ResourceIdHelper {

    public void insert(ResourceIdMap resourceIdMap) throws Exception {
        if (resourceIdMap == null) {
            throw new IllegalArgumentException("resourceIdMap is null");
        }
        EntityManager entityManager = ConnectionManager.getTransformEntityManager();
        entityManager.persist(resourceIdMap);
        entityManager.close();
    }

    public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ResourceIdMap c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.resourceType = :resource_type"
                + " and c.sourceId = :source_id";

        Query query = entityManager.createQuery(sql, ResourceIdMap.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("resource_type", resourceType.toString())
                .setParameter("source_id", sourceId.toString());

        ResourceIdMap ret = null;
        try {
            ret = (ResourceIdMap)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception {
        return getResourceIdMapByEdsId(resourceType, UUID.fromString(edsId));
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " ResourceIdMap c"
                + " where c.resourceType = :resource_type"
                + " and c.edsId = :eds_id";

        Query query = entityManager.createQuery(sql, ResourceIdMap.class)
                .setParameter("resource_type", resourceType.toString())
                .setParameter("eds_id", edsId.toString());

        ResourceIdMap ret = null;
        try {
            ret = (ResourceIdMap)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

}
