package org.endeavourhealth.core.database.rdbms.admin;

import org.endeavourhealth.core.database.dal.admin.PatientCohortDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.admin.models.RdbmsPatientCohort;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class RdbmsPatientCohortDal implements PatientCohortDalI {

    public boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsPatientCohort c"
                + " where c.ProtocolId = :protocol_id"
                + " AND c.ServiceId = :service_id"
                + " AND c.NhsNumber = :nhs_number"
                + " ORDER BY c.Inserted DESC";

        Query query = entityManager.createQuery(sql, RdbmsPatientCohort.class)
                .setParameter("protocol_id", protocolId.toString())
                .setParameter("service_id", serviceId.toString())
                .setParameter("nhs_number", nhsNumber)
                .setMaxResults(1); //select only 1, so we get the latest

        boolean ret = false;
        try {
            RdbmsPatientCohort o = (RdbmsPatientCohort)query.getSingleResult();
            ret = o.isInCohort();

        } catch (NoResultException ex) {
            ret = false;
        }

        entityManager.close();

        return ret;
    }
}
