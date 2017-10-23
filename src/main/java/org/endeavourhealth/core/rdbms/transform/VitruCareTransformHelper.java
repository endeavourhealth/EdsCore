package org.endeavourhealth.core.rdbms.transform;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Service;
import org.endeavourhealth.core.rdbms.transform.models.VitruCarePatientIdMap;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class VitruCareTransformHelper {

    public void saveVitruCareIdMapping(UUID edsPatientId, UUID serviceId, UUID systemId, String virtruCareId) throws Exception {
        VitruCarePatientIdMap o = new VitruCarePatientIdMap();
        o.setEdsPatientId(edsPatientId.toString());
        o.setServiceId(serviceId.toString());
        o.setSystemId(systemId.toString());
        o.setVitruCareId(virtruCareId);
        o.setCreatedAt(new DateTime());

        saveVitruCareIdMapping(o);
    }

    public void saveVitruCareIdMapping(VitruCarePatientIdMap mapping) throws Exception {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        EntityManager entityManager = ConnectionManager.getTransformEntityManager();
        entityManager.persist(mapping);
        entityManager.close();
    }

    public String getVitruCareId(UUID edsPatientId) throws Exception {
        EntityManager entityManager = ConnectionManager.getTransformEntityManager();

        String sql = "select c"
                + " from"
                + " VitruCarePatientIdMap c"
                + " where c.edsPatientId = :eds_patient_id";

        Query query = entityManager.createQuery(sql, Service.class)
                .setParameter("eds_patient_id", edsPatientId.toString());

        String ret = null;

        try {
            VitruCarePatientIdMap o = (VitruCarePatientIdMap)query.getSingleResult();
            ret = o.getVitruCareId();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
