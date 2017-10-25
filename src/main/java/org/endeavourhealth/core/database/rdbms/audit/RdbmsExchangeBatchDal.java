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
        entityManager.persist(dbObj);
        entityManager.close();
    }

    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeBatch c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString());

        List<RdbmsExchangeBatch> ret = query.getResultList();
        entityManager.close();

        return ret
                .stream()
                .map(T -> new ExchangeBatch(T))
                .collect(Collectors.toList());
    }

    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeBatch c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString())
                .setMaxResults(1);

        ExchangeBatch ret = null;
        try {
            RdbmsExchangeBatch result = (RdbmsExchangeBatch)query.getSingleResult();
            ret = new ExchangeBatch(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public ExchangeBatch getForExchangeAndBatchId(UUID exchangeId, UUID batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeBatch c"
                + " where c.exchangeId = :exchange_id"
                + " and c.batchId = :batch_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString())
                .setParameter("batch_id", batchId.toString())
                .setMaxResults(1);

        ExchangeBatch ret = null;
        try {
            RdbmsExchangeBatch result = (RdbmsExchangeBatch)query.getSingleResult();
            ret = new ExchangeBatch(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
