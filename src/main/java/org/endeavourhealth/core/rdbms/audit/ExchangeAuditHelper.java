package org.endeavourhealth.core.rdbms.audit;

import org.endeavourhealth.core.rdbms.ConnectionManager;
import org.endeavourhealth.core.rdbms.admin.models.Organisation;
import org.endeavourhealth.core.rdbms.audit.models.*;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ExchangeAuditHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ExchangeAuditHelper.class);

    public void save(Exchange exchange) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.persist(exchange);
        entityManager.close();
    }

    public void save(ExchangeEvent event) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(event);
        entityManager.close();
    }

    public void save(ExchangeTransformAudit exchangeTransformAudit) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.persist(exchangeTransformAudit);
        entityManager.close();
    }

    public void save(ExchangeTransformErrorState errorState) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.persist(errorState);
        entityManager.close();
    }


    public Exchange getExchange(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " Exchange c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, Exchange.class)
                .setParameter("id", exchangeId.toString());

        Exchange ret = null;
        try {
            ret = (Exchange)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }


    public void delete(ExchangeTransformErrorState errorState) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.remove(errorState);
        entityManager.close();
    }

    public ExchangeTransformAudit getMostRecentExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString())
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            ret = (ExchangeTransformAudit)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public ExchangeTransformErrorState getErrorState(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformErrorState c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformErrorState.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        ExchangeTransformErrorState ret = null;
        try {
            ret = (ExchangeTransformErrorState)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public List<ExchangeTransformErrorState> getAllErrorStates() throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformErrorState c";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class);
        List<ExchangeTransformErrorState> ret = query.getResultList();

        entityManager.close();

        return ret;
    }

    public boolean isServiceStarted(UUID serviceId, UUID systemId) throws Exception {

        //we assume a service is started if we've previously processed an exchange without error
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.errorXml is null"
                + " and c.started is not null";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        boolean started = false;

        try {
            ExchangeTransformAudit o = (ExchangeTransformAudit)query.getSingleResult();
            started = true;

        } catch (NoResultException ex) {
            started = false;
        }

        entityManager.close();

        return started;
    }

    /*public ExchangeTransformAudit getAnyExchangeTransformAudit(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            ret = (ExchangeTransformAudit)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }*/

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString());

        List<ExchangeTransformAudit> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString());

        List<ExchangeTransformAudit> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows, Date dateFrom, Date dateTo) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " Exchange c"
                + " where c.serviceId = :service_id"
                + " and c.systemId >= :date_from"
                + " and c.exchangeId <= :date_to";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("date_from", dateFrom)
                .setParameter("date_to", dateTo)
                .setMaxResults(maxRows);

        List<Exchange> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " Exchange c"
                + " where c.serviceId = :service_id"
                + " order by c.timestamp desc";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setMaxResults(maxRows);

        List<Exchange> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public List<ExchangeEvent> getExchangeEvents(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeEvent c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("exchange_id", exchangeId.toString());

        List<ExchangeEvent> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, DateTime timestamp) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id"
                + " and c.timestamp = :timestamp";

        Query query = entityManager.createQuery(sql, ExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString())
                .setParameter("timestamp", timestamp)
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            ret = (ExchangeTransformAudit)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    public List<UUID> getExchangeIdsForService(UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        /*Query q=session.createQuery("select sum(salary) from Emp");
        List<Integer> list=q.list();
        System.out.println(list.get(0));*/

        String sql = "select c.exchangeId"
                + " from"
                + " Exchange c"
                + " where c.serviceId = :service_id"
                + " order by timestamp DESC";

        Query query = entityManager.createQuery(sql)
                .setParameter("service_id", serviceId.toString());

        List<String> list = query.getResultList();

        List<UUID> ret = new ArrayList<>();
        for (String s: list) {
            UUID uuid = UUID.fromString(s);
            ret.add(uuid);
        }

        entityManager.close();

        return ret;
    }

    public void save(ExchangeBatch exchangeBatch) throws Exception {
        if (exchangeBatch == null) {
            throw new IllegalArgumentException("exchangeBatch is null");
        }

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(exchangeBatch);
        entityManager.close();
    }

    public List<ExchangeBatch> retrieveForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeBatch c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, ExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString());

        List<ExchangeBatch> ret = query.getResultList();
        entityManager.close();
        return ret;
    }

    public ExchangeBatch retrieveFirstForExchangeId(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeBatch c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, ExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString())
                .setMaxResults(1);

        ExchangeBatch ret = null;
        try {
            ret = (ExchangeBatch)query.getSingleResult();

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
                + " ExchangeBatch c"
                + " where c.exchangeId = :exchange_id"
                + " and c.batchId = :batch_id";

        Query query = entityManager.createQuery(sql, ExchangeBatch.class)
                .setParameter("exchange_id", exchangeId.toString())
                .setParameter("batch_id", batchId.toString())
                .setMaxResults(1);

        ExchangeBatch ret = null;
        try {
            ret = (ExchangeBatch)query.getSingleResult();

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }
}
