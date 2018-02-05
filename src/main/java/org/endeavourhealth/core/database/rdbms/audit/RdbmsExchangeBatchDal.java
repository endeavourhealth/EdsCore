package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeBatch;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsExchangeBatchDal implements ExchangeBatchDalI {


    public void save(ExchangeBatch exchangeBatch) throws Exception {
        if (exchangeBatch == null) {
            throw new IllegalArgumentException("exchangeBatch is null");
        }

        RdbmsExchangeBatch dbObj = new RdbmsExchangeBatch(exchangeBatch);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //persist only works for initial inserts not updates, so changing to use upsert
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_batch"
                    + " (exchange_id, batch_id, inserted_at, eds_patient_id)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " eds_patient_id = VALUES(eds_patient_id);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getExchangeId());
            ps.setString(2, dbObj.getBatchId());
            ps.setTimestamp(3, new java.sql.Timestamp(dbObj.getInsertedAt().getTime()));
            if (dbObj.getEdsPatientId() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, dbObj.getEdsPatientId());
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeBatch c"
                    + " where c.exchangeId = :exchange_id"
                    + " order by c.insertedAt ASC";

            Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                    .setParameter("exchange_id", exchangeId.toString());

            List<RdbmsExchangeBatch> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ExchangeBatch(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeBatch c"
                    + " where c.exchangeId = :exchange_id"
                    + " order by c.insertedAt ASC";

            Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                    .setParameter("exchange_id", exchangeId.toString())
                    .setMaxResults(1);

            RdbmsExchangeBatch result = (RdbmsExchangeBatch)query.getSingleResult();
            return new ExchangeBatch(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeBatch c"
                    + " where c.exchangeId = :exchange_id"
                    + " and c.batchId = :batch_id";

            Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                    .setParameter("exchange_id", exchangeId.toString())
                    .setParameter("batch_id", batchId.toString())
                    .setMaxResults(1);


            RdbmsExchangeBatch result = (RdbmsExchangeBatch)query.getSingleResult();
            return new ExchangeBatch(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }
}
