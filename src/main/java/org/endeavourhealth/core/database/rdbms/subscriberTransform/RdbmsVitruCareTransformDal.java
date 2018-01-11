package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.VitruCareTransformDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsVitruCarePatientIdMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

public class RdbmsVitruCareTransformDal implements VitruCareTransformDalI {

    private String subscriberConfigName = null;

    public RdbmsVitruCareTransformDal(String subscriberConfigName) {
        this.subscriberConfigName = subscriberConfigName;
    }

    public void saveVitruCareIdMapping(UUID edsPatientId, UUID serviceId, UUID systemId, String virtruCareId) throws Exception {
        RdbmsVitruCarePatientIdMap o = new RdbmsVitruCarePatientIdMap();
        o.setEdsPatientId(edsPatientId.toString());
        o.setServiceId(serviceId.toString());
        o.setVitruCareId(virtruCareId);
        o.setCreatedAt(new Date());

        saveVitruCareIdMapping(o);
    }

    private void saveVitruCareIdMapping(RdbmsVitruCarePatientIdMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(mapping);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public String getVitruCareId(UUID edsPatientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsVitruCarePatientIdMap c"
                    + " where c.edsPatientId = :eds_patient_id";

            Query query = entityManager.createQuery(sql, RdbmsVitruCarePatientIdMap.class)
                    .setParameter("eds_patient_id", edsPatientId.toString());

            RdbmsVitruCarePatientIdMap o = (RdbmsVitruCarePatientIdMap)query.getSingleResult();
            return o.getVitruCareId();

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }
}
