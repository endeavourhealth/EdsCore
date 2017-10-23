package org.endeavourhealth.core.rdbms.subscriber;

import org.endeavourhealth.core.rdbms.subscriber.models.PseudoIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class PseudoIdHelper {

    public static void storePseudoId(String patientId, String enterpriseConfigName, String pseudoId) throws Exception {

        EntityManager entityManager = SubscriberConnectionMananger.getEntityManager(enterpriseConfigName);

        PseudoIdMap map = findIdMap(patientId, entityManager);
        if (map == null) {
            map = new PseudoIdMap();
            map.setPatientId(patientId);
        }
        map.setPseudoId(pseudoId);

        entityManager.getTransaction().begin();
        entityManager.persist(map);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static PseudoIdMap findIdMap(String patientId, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " PseudoIdMap c"
                + " where c.patientId = :patientId";


        Query query = entityManager.createQuery(sql, PseudoIdMap.class)
                .setParameter("patientId", patientId);

        try {
            return (PseudoIdMap)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }
}
