package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.PseudoIdDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPseudoIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsPseudoIdDal implements PseudoIdDalI {

    private String subscriberConfigName = null;

    public RdbmsPseudoIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public void storePseudoId(String patientId, String pseudoId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPseudoIdMap map = findIdMap(patientId, entityManager);
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
    public String findPseudoId(String patientId) throws Exception {

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            RdbmsPseudoIdMap result = findIdMap(patientId, entityManager);

            if (result != null) {
                return result.getPseudoId();
            } else {
                return null;
            }

        } finally {
            entityManager.close();
        }
    }

    public List<String> findPatientIdsFromPseudoIds(List<String> pseudoIds) throws Exception {

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
