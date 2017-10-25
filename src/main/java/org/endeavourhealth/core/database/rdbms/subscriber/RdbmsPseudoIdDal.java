package org.endeavourhealth.core.database.rdbms.subscriber;

import org.endeavourhealth.core.database.dal.subscriber.PseudoIdDalI;
import org.endeavourhealth.core.database.rdbms.subscriber.models.RdbmsPseudoIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RdbmsPseudoIdDal implements PseudoIdDalI {

    private String subscriberConfigName = null;

    public RdbmsPseudoIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public void storePseudoId(String patientId, String pseudoId) throws Exception {

        EntityManager entityManager = SubscriberConnectionMananger.getEntityManager(subscriberConfigName);

        RdbmsPseudoIdMap map = findIdMap(patientId, entityManager);
        if (map == null) {
            map = new RdbmsPseudoIdMap();
            map.setPatientId(patientId);
        }
        map.setPseudoId(pseudoId);

        entityManager.getTransaction().begin();
        entityManager.persist(map);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private RdbmsPseudoIdMap findIdMap(String patientId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsPseudoIdMap c"
                + " where c.patientId = :patientId";


        Query query = entityManager.createQuery(sql, RdbmsPseudoIdMap.class)
                .setParameter("patientId", patientId);

        try {
            return (RdbmsPseudoIdMap)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }
}
