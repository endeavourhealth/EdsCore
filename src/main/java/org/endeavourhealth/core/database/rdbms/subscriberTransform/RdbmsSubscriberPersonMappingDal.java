package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.SubscriberPersonMappingDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsEnterprisePersonIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsSubscriberPersonMappingDal implements SubscriberPersonMappingDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsSubscriberPersonMappingDal.class);

    private String subscriberConfigName = null;

    public RdbmsSubscriberPersonMappingDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public Long findOrCreateEnterprisePersonId(String discoveryPersonId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            Long ret = findEnterprisePersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            return createEnterprisePersonId(discoveryPersonId, entityManager);

        } catch (Exception ex) {
            //if another thread has beat us to it, we'll get an exception, so try the find again
            Long ret = findEnterprisePersonId(discoveryPersonId, entityManager);
            if (ret != null) {
                return ret;
            }

            throw ex;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Long> findEnterprisePersonIdsForPersonId(String discoveryPersonId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsEnterprisePersonIdMap c"
                    + " where c.personId = :personId";

            Query query = entityManager.createQuery(sql, RdbmsEnterprisePersonIdMap.class)
                    .setParameter("personId", discoveryPersonId);

            List<RdbmsEnterprisePersonIdMap> ret = query.getResultList();
            return ret
                    .stream()
                    .map(T -> T.getEnterprisePersonId())
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }


    private static Long findEnterprisePersonId(String discoveryPersonId, EntityManager entityManager) {

        String sql = "select c"
                + " from"
                + " RdbmsEnterprisePersonIdMap c"
                + " where c.personId = :personId";


        Query query = entityManager.createQuery(sql, RdbmsEnterprisePersonIdMap.class)
                .setParameter("personId", discoveryPersonId);

        try {
            RdbmsEnterprisePersonIdMap result = (RdbmsEnterprisePersonIdMap)query.getSingleResult();
            return result.getEnterprisePersonId();

        } catch (NoResultException ex) {
            return null;
        }
    }

    private static Long createEnterprisePersonId(String discoveryPersonId, EntityManager entityManager) throws Exception {

        RdbmsEnterprisePersonIdMap mapping = new RdbmsEnterprisePersonIdMap();
        mapping.setPersonId(discoveryPersonId);

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

            return mapping.getEnterprisePersonId();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }
    }

    /*public Long findEnterprisePersonId(String discoveryPersonId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            return findEnterprisePersonId(discoveryPersonId, entityManager);

        } finally {
            entityManager.close();
        }
    }*/
}
