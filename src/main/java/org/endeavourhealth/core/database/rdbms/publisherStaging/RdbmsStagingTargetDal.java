package org.endeavourhealth.core.database.rdbms.publisherStaging;

import org.endeavourhealth.core.database.dal.publisherStaging.StagingTargetDalI;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.publisherStaging.models.RdbmsStagingTarget;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class RdbmsStagingTargetDal implements StagingTargetDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsStagingTargetDal.class);

    @Override
    public void processStagingForTarget(UUID exchangeId, UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getPublisherStagingEntityMananger(serviceId);
        CallableStatement stmt = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "{call process_procedure_staging_exchange(?)}";
            stmt = connection.prepareCall(sql);

            entityManager.getTransaction().begin();

            stmt.setString(1, exchangeId.toString());

            stmt.execute();

            entityManager.getTransaction().commit();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            entityManager.close();
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
                    .setParameter("exchange_id", exchangeId.toString());

            List<RdbmsStagingTarget> resultList = query.getResultList();
            return resultList;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
