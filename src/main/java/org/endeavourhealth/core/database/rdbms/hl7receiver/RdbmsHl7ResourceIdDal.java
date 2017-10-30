package org.endeavourhealth.core.database.rdbms.hl7receiver;

import org.endeavourhealth.core.database.dal.hl7receiver.Hl7ResourceIdDalI;
import org.endeavourhealth.core.database.dal.hl7receiver.models.ResourceId;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.hl7receiver.models.RdbmsResourceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class RdbmsHl7ResourceIdDal implements Hl7ResourceIdDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsHl7ResourceIdDal.class);

    public ResourceId getResourceId(String scope, String resource, String uniqueId) throws Exception {
        EntityManager entityManager = ConnectionManager.getHl7ReceiverEntityManager();

        try {
            if (!entityManager.isOpen())
                throw new IllegalStateException("No connection to HL7 DB");

            String sql = "select c"
                    + " from"
                    + " RdbmsResourceId c"
                    + " where c.scopeId = :scopeId"
                    + " and c.resourceType = :resourceType"
                    + " and c.uniqueId = :uniqueId";

            Query query = entityManager.createQuery(sql, RdbmsResourceId.class)
                    .setParameter("scopeId", scope).setParameter("resourceType", resource).setParameter("uniqueId", uniqueId);

            if (query == null) {
                LOG.trace("Failed to create query");
                LOG.trace("scopeId [" + scope + "]");
                LOG.trace("resourceType [" + resource + "]");
                LOG.trace("uniqueId [" + uniqueId + "]");
                throw new IllegalStateException("Failed to create query");
            }

            List results = query.getResultList();
            if (results.isEmpty())
                return null;

            RdbmsResourceId result = (RdbmsResourceId) results.get(0);
            if (result != null) {
                LOG.trace("Read recourceId:" + result.getUniqueId() + "==>" + result.getResourceId());
                return new ResourceId(result);
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public void saveResourceId(ResourceId resourceId)  throws Exception {

        RdbmsResourceId dbObj = new RdbmsResourceId(resourceId);

        EntityManager entityManager = ConnectionManager.getHl7ReceiverEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();
            LOG.trace("Saved recourceId:" + resourceId.getUniqueId() + "==>" + resourceId.getResourceId());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }


}
