package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.PatientCohortDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsPatientCohort;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.UUID;

public class RdbmsPatientCohortDal implements PatientCohortDalI {

    @Override
    public void saveInCohort(UUID protocolId, UUID serviceId, String nhsNumber, boolean inCohort) throws Exception {

        RdbmsPatientCohort obj = new RdbmsPatientCohort();
        obj.setInserted(new Date());
        obj.setInCohort(inCohort);
        obj.setNhsNumber(nhsNumber);
        obj.setServiceId(serviceId.toString());
        obj.setProtocolId(protocolId.toString());

        EntityManager entityManager = ConnectionManager.getAdminEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(obj);
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }


    public boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        try {

            String sql = "select c"
                    + " from"
                    + " RdbmsPatientCohort c"
                    + " where c.protocolId = :protocol_id"
                    + " AND c.serviceId = :service_id"
                    + " AND c.nhsNumber = :nhs_number"
                    + " ORDER BY c.inserted DESC";

            Query query = entityManager.createQuery(sql, RdbmsPatientCohort.class)
                    .setParameter("protocol_id", protocolId.toString())
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("nhs_number", nhsNumber)
                    .setMaxResults(1); //select only 1, so we get the latest

            RdbmsPatientCohort o = (RdbmsPatientCohort)query.getSingleResult();
            return o.isInCohort();

        } catch (NoResultException ex) {
            return false;

        } finally {
            entityManager.close();
        }
    }
}
