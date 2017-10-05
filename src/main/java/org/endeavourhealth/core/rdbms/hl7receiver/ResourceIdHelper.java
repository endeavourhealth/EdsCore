package org.endeavourhealth.core.rdbms.hl7receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public class ResourceIdHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceIdHelper.class);

    public static ResourceId getResourceId(String scope, String resource, String uniqueId) throws Exception {
        EntityManager entityManager = HL7ReceiverConnection.getEntityManager();

        if (!entityManager.isOpen())
            throw new IllegalStateException("No connection to HL7 DB");

        String sql = "select c"
                + " from"
                + " ResourceId c"
                + " where c.scopeId = :scopeId"
                + " and c.resourceType = :resourceType"
                + " and c.uniqueId = :uniqueId";

        Query query = entityManager.createQuery(sql, ResourceId.class)
                .setParameter("scopeId", scope).setParameter("resourceType", resource).setParameter("uniqueId", uniqueId);

        List results = query.getResultList();
        if (results.isEmpty())
            return null;

        ResourceId ret = (ResourceId)results.get(0);

        LOG.trace("Read recourceId:" + ret.getUniqueId() + "==>" + ret.getResourceId());
        return ret;

    }

    public static void saveResourceId(ResourceId resourceId)  throws Exception {
        EntityManager entityManager = HL7ReceiverConnection.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(resourceId);
            entityManager.getTransaction().commit();
            LOG.trace("Saved recourceId:" + resourceId.getUniqueId() + "==>" + resourceId.getResourceId());
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
    }


}
