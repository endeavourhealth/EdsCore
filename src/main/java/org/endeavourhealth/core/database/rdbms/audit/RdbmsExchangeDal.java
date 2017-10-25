package org.endeavourhealth.core.database.rdbms.audit;

import org.endeavourhealth.core.database.dal.audit.ExchangeDalI;
import org.endeavourhealth.core.database.dal.audit.models.Exchange;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeEvent;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformAudit;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformErrorState;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchange;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeEvent;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeTransformAudit;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchangeTransformErrorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsExchangeDal implements ExchangeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsExchangeDal.class);

    public void save(Exchange exchange) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        entityManager.persist(exchange);
        entityManager.close();
    }

    public void save(ExchangeEvent event) throws Exception {

        RdbmsExchangeEvent dbObj = new RdbmsExchangeEvent(event);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(dbObj);
        entityManager.close();
    }

    public void save(ExchangeTransformAudit exchangeTransformAudit) throws Exception {

        RdbmsExchangeTransformAudit dbObj = new RdbmsExchangeTransformAudit(exchangeTransformAudit);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(dbObj);
        entityManager.close();
    }

    public void save(ExchangeTransformErrorState errorState) throws Exception {

        RdbmsExchangeTransformErrorState dbObj = new RdbmsExchangeTransformErrorState(errorState);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        entityManager.persist(dbObj);
        entityManager.close();
    }


    public Exchange getExchange(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchange c"
                + " where c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsExchange.class)
                .setParameter("id", exchangeId.toString());

        Exchange ret = null;
        try {
            RdbmsExchange result = (RdbmsExchange)query.getSingleResult();
            ret = new Exchange(result);

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
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id"
                + " order by c.started DESC";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString())
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            RdbmsExchangeTransformAudit result = (RdbmsExchangeTransformAudit)query.getSingleResult();
            ret = new ExchangeTransformAudit(result);

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
                + " RdbmsExchangeTransformErrorState c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformErrorState.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        ExchangeTransformErrorState ret = null;
        try {
            RdbmsExchangeTransformErrorState result = (RdbmsExchangeTransformErrorState)query.getSingleResult();
            ret = new ExchangeTransformErrorState(result);

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
                + " RdbmsExchangeTransformErrorState c";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class);
        List<RdbmsExchangeTransformErrorState> results = query.getResultList();

        entityManager.close();

        //can't use streams as the constructor is declared as throwing an exception
        List<ExchangeTransformErrorState> ret = new ArrayList<>();
        for (RdbmsExchangeTransformErrorState result: results) {
            ret.add(new ExchangeTransformErrorState(result));
        }
        return ret;
    }

    public boolean isServiceStarted(UUID serviceId, UUID systemId) throws Exception {

        //we assume a service is started if we've previously processed an exchange without error
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.errorXml is null"
                + " and c.started is not null";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setMaxResults(1);

        boolean started = false;

        try {
            RdbmsExchangeTransformAudit o = (RdbmsExchangeTransformAudit)query.getSingleResult();
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
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString());

        List<RdbmsExchangeTransformAudit> ret = query.getResultList();
        entityManager.close();
        return ret
                .stream()
                .map(T -> new ExchangeTransformAudit(T))
                .collect(Collectors.toList());
    }

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString());

        List<RdbmsExchangeTransformAudit> ret = query.getResultList();
        entityManager.close();
        return ret
                .stream()
                .map(T -> new ExchangeTransformAudit(T))
                .collect(Collectors.toList());
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows, Date dateFrom, Date dateTo) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchange c"
                + " where c.serviceId = :service_id"
                + " and c.systemId >= :date_from"
                + " and c.exchangeId <= :date_to";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("date_from", dateFrom)
                .setParameter("date_to", dateTo)
                .setMaxResults(maxRows);

        List<RdbmsExchange> results = query.getResultList();
        entityManager.close();

        List<Exchange> ret = new ArrayList<>();
        for (RdbmsExchange result: results) {
            ret.add(new Exchange(result));
        }
        return ret;
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchange c"
                + " where c.serviceId = :service_id"
                + " order by c.timestamp desc";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setMaxResults(maxRows);

        List<RdbmsExchange> results = query.getResultList();
        entityManager.close();

        List<Exchange> ret = new ArrayList<>();
        for (RdbmsExchange result: results) {
            ret.add(new Exchange(result));
        }
        return ret;
    }

    public List<ExchangeEvent> getExchangeEvents(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeEvent c"
                + " where c.exchangeId = :exchange_id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("exchange_id", exchangeId.toString());

        List<RdbmsExchangeEvent> ret = query.getResultList();
        entityManager.close();
        return ret
                .stream()
                .map(T -> new ExchangeEvent(T))
                .collect(Collectors.toList());
    }

    /*public ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, Date timestamp) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " ExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id"
                + " and c.timestamp = :timestamp";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString())
                .setParameter("timestamp", timestamp)
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            RdbmsExchangeTransformAudit result = (RdbmsExchangeTransformAudit)query.getSingleResult();
            ret = new ExchangeTransformAudit(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }*/

    public List<UUID> getExchangeIdsForService(UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        /*Query q=session.createQuery("select sum(salary) from Emp");
        List<Integer> list=q.list();
        System.out.println(list.get(0));*/

        String sql = "select c.exchangeId"
                + " from"
                + " RdbmsExchange c"
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

    public ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c"
                + " from"
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " and c.exchangeId = :exchange_id"
                + " and c.id = :id";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString())
                .setParameter("exchange_id", exchangeId.toString())
                .setParameter("id", id.toString())
                .setMaxResults(1);

        ExchangeTransformAudit ret = null;
        try {
            RdbmsExchangeTransformAudit result = (RdbmsExchangeTransformAudit)query.getSingleResult();
            ret = new ExchangeTransformAudit(result);

        } catch (NoResultException ex) {
            //do nothing
        }

        entityManager.close();

        return ret;
    }

    @Override
    public List<ExchangeTransformAudit> getAllExchangeTransformAuditsForService(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        String sql = "select c.exchangeId, c.numberBatchesCreated"
                + " from"
                + " RdbmsExchangeTransformAudit c"
                + " where c.serviceId = :service_id"
                + " and c.systemId = :system_id"
                + " order by c.started desc";

        Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                .setParameter("service_id", serviceId.toString())
                .setParameter("system_id", systemId.toString());

        List<RdbmsExchangeTransformAudit> ret = query.getResultList();
        entityManager.close();
        return ret
                .stream()
                .map(T -> new ExchangeTransformAudit(T))
                .collect(Collectors.toList());
    }

}
