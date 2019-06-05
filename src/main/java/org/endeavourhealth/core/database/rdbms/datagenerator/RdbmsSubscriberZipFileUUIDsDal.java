package org.endeavourhealth.core.database.rdbms.datagenerator;

import org.endeavourhealth.core.database.dal.datagenerator.SubscriberZipFileUUIDsDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datagenerator.models.RdbmsSubscriberZipFileUUIDs;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class RdbmsSubscriberZipFileUUIDsDal implements SubscriberZipFileUUIDsDalI {

    public RdbmsSubscriberZipFileUUIDs getSubscriberZipFileUUIDsEntity(String queuedMessageUUID) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            RdbmsSubscriberZipFileUUIDs ret = entityManager.find(RdbmsSubscriberZipFileUUIDs.class, queuedMessageUUID);
            return ret;

        } catch (Exception ex){
            return null;

        } finally {
            entityManager.close();
        }

    }

    public List<RdbmsSubscriberZipFileUUIDs> getAllSubscriberZipFileUUIDsEntities() throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RdbmsSubscriberZipFileUUIDs> cq = cb.createQuery(RdbmsSubscriberZipFileUUIDs.class);
            TypedQuery<RdbmsSubscriberZipFileUUIDs> query = entityManager.createQuery(cq);
            List<RdbmsSubscriberZipFileUUIDs> ret = query.getResultList();
            return ret;

        } catch (Exception ex) {
            throw ex;

        } finally {
            entityManager.close();
        }

    }

    public RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(RdbmsSubscriberZipFileUUIDs rszfu) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {

            String sql = "select max(filing_order) from data_generator.subscriber_zip_file_uuids;";
            Query query = entityManager.createNativeQuery(sql);
            Long result = (Long) query.getSingleResult();

            if (result == null) {
                rszfu.setFilingOrder(1);
            } else {
                rszfu.setFilingOrder(result + 1);
            }

            entityManager.getTransaction().begin();
            entityManager.persist(rszfu);
            entityManager.getTransaction().commit();
            return rszfu;

        } catch (Exception ex) {

            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(int subscriberId, String queuedMessageId,
                                                                          String queuedMessageBody) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            RdbmsSubscriberZipFileUUIDs rszfu = new RdbmsSubscriberZipFileUUIDs();

            rszfu.setSubscriberId(subscriberId);
            rszfu.setQueuedMessageUUID(queuedMessageId);
            rszfu.setQueuedMessageBody(queuedMessageBody);

            String sql = "select max(filing_order) from data_generator.subscriber_zip_file_uuids;";
            Query query = entityManager.createNativeQuery(sql);
            Long result = (Long) query.getSingleResult();

            if (result == null) {
                rszfu.setFilingOrder(1);
            } else {
                rszfu.setFilingOrder(result + 1);
            }

            entityManager.getTransaction().begin();
            entityManager.persist(rszfu);
            entityManager.getTransaction().commit();
            return rszfu;

        } catch (Exception ex) {

            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

}
