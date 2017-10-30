package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeBatchDalI;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeBatch;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeBatch;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
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
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }
    }

    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeBatch c"
                    + " where c.exchangeId = :exchange_id";

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
