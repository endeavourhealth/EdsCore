package org.endeavourhealth.core.rdbms.admin;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.admin.models.PatientCohort;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.UUID;

public class PatientCohortHelper {

    public boolean isInCohort(UUID protocolId, UUID serviceId, String nhsNumber) throws Exception {
        EntityManager entityManager = ConnectionManager.getAdminEntityManager();

        String sql = "select c"
                + " from"
                + " PatientCohort c"
                + " where c.ProtocolId = :protocol_id"
                + " AND c.ServiceId = :service_id"
                + " AND c.NhsNumber = :nhs_number";

        Query query = entityManager.createQuery(sql, PatientCohort.class)
                .setParameter("protocol_id", protocolId.toString())
                .setParameter("service_id", serviceId.toString())
                .setParameter("nhs_number", nhsNumber)
                .setMaxResults(1); //select only 1, so we get the latest

        boolean ret = false;
        try {
            PatientCohort o = (PatientCohort)query.getSingleResult();
            ret = o.isInCohort();

        } catch (NoResultException ex) {
            ret = false;
        }

        entityManager.close();

        return ret;
    }
}
