package org.endeavourhealth.core.database.rdbms.subscriberTransform;

import org.endeavourhealth.core.database.dal.subscriberTransform.VitruCareTransformDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsService;
import org.endeavourhealth.core.database.rdbms.subscriberTransform.models.RdbmsVitruCarePatientIdMap;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
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
        o.setSystemId(systemId.toString());
        o.setVitruCareId(virtruCareId);
        o.setCreatedAt(new DateTime());

        saveVitruCareIdMapping(o);
    }

    private void saveVitruCareIdMapping(RdbmsVitruCarePatientIdMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);
        entityManager.persist(mapping);
        entityManager.close();
    }

    public String getVitruCareId(UUID edsPatientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getSubscriberTransformEntityManager(subscriberConfigName);

        String sql = "select c"
                + " from"
                + " RdbmsVitruCarePatientIdMap c"
                + " where c.edsPatientId = :eds_patient_id";

        Query query = entityManager.createQuery(sql, RdbmsService.class)
                .setParameter("eds_patient_id", edsPatientId.toString());

        String ret = null;

        try {
            RdbmsVitruCarePatientIdMap o = (RdbmsVitruCarePatientIdMap)query.getSingleResult();
            ret = o.getVitruCareId();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
