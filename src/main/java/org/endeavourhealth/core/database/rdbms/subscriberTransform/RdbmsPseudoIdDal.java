package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.endeavourhealth.core.database.dal.subscriberTransform.PseudoIdDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsPseudoIdMap;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsSubscriberPseudoId;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class RdbmsPseudoIdDal implements PseudoIdDalI {

    private String subscriberConfigName = null;

    public RdbmsPseudoIdDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    @Override
    public void auditPseudoId(String saltName, TreeMap<String, String> keys, String pseudoId) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = new ObjectNode(mapper.getNodeFactory());

        List<String> values = new ArrayList<>();
        Set set = keys.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            String key = (String)me.getKey();
            String val = (String)me.getValue();
            root.put(key, val);
        }
        String sourceValueStr = mapper.writeValueAsString(root);

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        PreparedStatement ps = null;

        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO pseudo_id_audit (salt_key_name, source_values, pseudo_id)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " pseudo_id = VALUES(pseudo_id)";

            ps = connection.prepareStatement(sql);

            entityManager.getTransaction().begin();

            int col = 1;
            ps.setString(col++, saltName);
            ps.setString(col++, sourceValueStr);
            ps.setString(col++, pseudoId);

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
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

    /*@Override
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
