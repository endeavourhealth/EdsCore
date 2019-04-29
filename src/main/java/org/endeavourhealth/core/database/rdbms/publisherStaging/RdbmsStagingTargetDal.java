package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingTargetDal implements StagingTargetDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingTargetDal.class);

    @Override
    public void processStagingForTarget(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);

        try {

            StoredProcedureQuery spQuery
                    = entityManager.createStoredProcedureQuery("process_procedure_staging_exchange")
                    .setParameter("_exchange_id", exchangeId);
            spQuery.execute();

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<RdbmsStagingTarget> getTargetProcedures(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);

        try {
            String sql = "select c"
                    + " from "
                    + " RdbmsStagingTarget c"
                    + " where c.exchangeId = :exchange_id";

            Query query = entityManager.createQuery(sql, RdbmsStagingTarget.class)
                    .setParameter("exchange_id", exchangeId);

            List<RdbmsStagingTarget> resultList = query.getResultList();
            return resultList;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
