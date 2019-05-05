package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.PseudoIdDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPseudoIdMap;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsSubscriberPseudoId;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class RdbmsPseudoIdDal implements PseudoIdDalI {

    private String subscriberConfigName = null;

    public RdbmsPseudoIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public void storePseudoIdOldWay(String patientId, String pseudoId) throws Exception {


        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPseudoIdMap map = findIdMapOldWay(patientId, entityManager);
            if (map == null) {
                map = new RdbmsPseudoIdMap();
                map.setPatientId(patientId);
            }
            map.setPseudoId(pseudoId);

            entityManager.getTransaction().begin();
            entityManager.persist(map);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public String findPseudoIdOldWay(String patientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPseudoIdMap result = findIdMapOldWay(patientId, entityManager);

            if (result != null) {
                return result.getPseudoId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    /*public List<String> findPatientIdsFromPseudoIds(List<String> pseudoIds) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RdbmsPseudoIdMap> cq = cb.createQuery(RdbmsPseudoIdMap.class);
            Root<RdbmsPseudoIdMap> rootEntry = cq.from(RdbmsPseudoIdMap.class);

            Predicate predicate = rootEntry.get("pseudoId").in(pseudoIds);
            cq.where(predicate);

            TypedQuery<RdbmsPseudoIdMap> query = entityManager.createQuery(cq);

            entityManager.close();

            try {
                List<RdbmsPseudoIdMap> maps = query.getResultList();

                List<String> patientIds = maps.stream()
                                            .map(RdbmsPseudoIdMap::getPatientId)
                                            .collect(Collectors.toList());
                return patientIds;

            } catch (NoResultException ex) {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }*/

    private RdbmsPseudoIdMap findIdMapOldWay(String patientId, EntityManager entityManager) throws Exception {

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


    @Override
    public void saveSubscriberPseudoId(UUID patientId, long subscriberPatientId, String saltKeyName, String pseudoId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsSubscriberPseudoId map = findSubscriberPseudoId(patientId, saltKeyName, entityManager);
            if (map == null) {
                map = new RdbmsSubscriberPseudoId();
                map.setPatientId(patientId.toString());
                map.setSubscriberPatientId(subscriberPatientId);
                map.setSaltKeyName(saltKeyName);
            }
            map.setPseudoId(pseudoId);

            entityManager.getTransaction().begin();
            entityManager.persist(map);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public String findSubscriberPseudoId(UUID patientId, String saltKeyName) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsSubscriberPseudoId result = findSubscriberPseudoId(patientId, saltKeyName, entityManager);

            if (result != null) {
                return result.getPseudoId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    private RdbmsSubscriberPseudoId findSubscriberPseudoId(UUID patientId, String saltKeyName, EntityManager entityManager) throws Exception {

        String sql = "select c"
                + " from"
                + " RdbmsSubscriberPseudoId c"
                + " where c.patientId = :patientId"
                + " and c.saltKeyName = :saltKeyName";


        Query query = entityManager.createQuery(sql, RdbmsSubscriberPseudoId.class)
                .setParameter("patientId", patientId.toString())
                .setParameter("saltKeyName", saltKeyName);

        try {
            return (RdbmsSubscriberPseudoId)query.getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }
}
