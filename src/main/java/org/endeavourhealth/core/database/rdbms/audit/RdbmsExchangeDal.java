package org.endeavourhealth.core.database.rdbms.audit;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Strings;
import org.endeavourhealth.core.database.dal.audit.ExchangeDalI;
import org.endeavourhealth.core.database.dal.audit.models.*;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.audit.models.*;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RdbmsExchangeDal implements ExchangeDalI {
    private static final Logger LOG = LoggerFactory.getLogger(RdbmsExchangeDal.class);

    public void save(Exchange exchange) throws Exception {

        RdbmsExchange dbObj = new RdbmsExchange(exchange);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange"
                    + " (id, timestamp, headers, service_id, body)"
                    + " VALUES (?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " timestamp = VALUES(timestamp),"
                    + " headers = VALUES(headers),"
                    + " service_id = VALUES(service_id),"
                    + " body = VALUES(body);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getId());
            ps.setTimestamp(2, new java.sql.Timestamp(dbObj.getTimestamp().getTime()));
            ps.setString(3, dbObj.getHeaders());
            ps.setString(4, dbObj.getServiceId());
            ps.setString(5, dbObj.getBody());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public void save(ExchangeEvent event) throws Exception {

        RdbmsExchangeEvent dbObj = new RdbmsExchangeEvent(event);
        dbObj.setId(UUIDs.timeBased().toString()); //not set by the proxy constructor but needed

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(dbObj);
            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
        }

    }

    public void save(ExchangeTransformAudit exchangeTransformAudit) throws Exception {

        RdbmsExchangeTransformAudit dbObj = new RdbmsExchangeTransformAudit(exchangeTransformAudit);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_transform_audit"
                    + " (id, service_id, system_id, exchange_id, started, ended, error_xml, resubmitted, deleted, number_batches_created)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " started = VALUES(started),"
                    + " ended = VALUES(ended),"
                    + " error_xml = VALUES(error_xml),"
                    + " resubmitted = VALUES(resubmitted),"
                    + " deleted = VALUES(deleted),"
                    + " number_batches_created = VALUES(number_batches_created);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getId());
            ps.setString(2, dbObj.getServiceId());
            ps.setString(3, dbObj.getSystemId());
            ps.setString(4, dbObj.getExchangeId());
            if (dbObj.getStarted() != null) {
                ps.setTimestamp(5, new java.sql.Timestamp(dbObj.getStarted().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            if (dbObj.getEnded() != null) {
                ps.setTimestamp(6, new java.sql.Timestamp(dbObj.getEnded().getTime()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            if (!Strings.isNullOrEmpty(dbObj.getErrorXml())) {
                ps.setString(7, dbObj.getErrorXml());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setBoolean(8, dbObj.isResubmitted());
            if (dbObj.getDeleted() != null) {
                ps.setTimestamp(9, new java.sql.Timestamp(dbObj.getDeleted().getTime()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            if (dbObj.getNumberBatchesCreated() != null) {
                ps.setInt(10, dbObj.getNumberBatchesCreated());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }

    }

    public void save(ExchangeTransformErrorState errorState) throws Exception {

        RdbmsExchangeTransformErrorState dbObj = new RdbmsExchangeTransformErrorState(errorState);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_transform_error_state"
                    + " (service_id, system_id, exchange_ids_in_error)"
                    + " VALUES (?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " exchange_ids_in_error = VALUES(exchange_ids_in_error);";

            ps = connection.prepareStatement(sql);
            ps.setString(1, dbObj.getServiceId());
            ps.setString(2, dbObj.getSystemId());
            ps.setString(3, dbObj.getExchangeIdsInError());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }


    public Exchange getExchange(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchange c"
                    + " where c.id = :id";

            Query query = entityManager.createQuery(sql, RdbmsExchange.class)
                    .setParameter("id", exchangeId.toString());

            RdbmsExchange result = (RdbmsExchange)query.getSingleResult();
            return new Exchange(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }


    public void delete(ExchangeTransformErrorState errorState) throws Exception {

        RdbmsExchangeTransformErrorState dbObj = new RdbmsExchangeTransformErrorState(errorState);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;
        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support deleting without retrieving
            //entityManager.remove(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "DELETE FROM exchange_transform_error_state"
                    + " WHERE service_id = ?"
                    + " AND system_id = ?;";

            ps = connection.prepareStatement(sql);
            ps.setString(1, dbObj.getServiceId());
            ps.setString(2, dbObj.getSystemId());

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    public ExchangeTransformAudit getMostRecentExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
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

            RdbmsExchangeTransformAudit result = (RdbmsExchangeTransformAudit)query.getSingleResult();
            return new ExchangeTransformAudit(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public ExchangeTransformErrorState getErrorState(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeTransformErrorState c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id";

            Query query = entityManager.createQuery(sql, RdbmsExchangeTransformErrorState.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setMaxResults(1);

            RdbmsExchangeTransformErrorState result = (RdbmsExchangeTransformErrorState)query.getSingleResult();
            return new ExchangeTransformErrorState(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    public List<ExchangeTransformErrorState> getAllErrorStates() throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeTransformErrorState c";

            Query query = entityManager.createQuery(sql, RdbmsExchangeTransformErrorState.class);
            List<RdbmsExchangeTransformErrorState> results = query.getResultList();

            //can't use streams as the constructor is declared as throwing an exception
            List<ExchangeTransformErrorState> ret = new ArrayList<>();
            for (RdbmsExchangeTransformErrorState result : results) {
                ret.add(new ExchangeTransformErrorState(result));
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public boolean isServiceStarted(UUID serviceId, UUID systemId) throws Exception {

        //we assume a service is started if we've previously processed an exchange without error
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeTransformAudit c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " and c.errorXml is null"
                    + " and c.deleted is null"
                    + " and c.started is not null";

            Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString())
                    .setMaxResults(1);

            RdbmsExchangeTransformAudit o = (RdbmsExchangeTransformAudit)query.getSingleResult();
            //if we find an audit, we've started
            return true;

        } catch (NoResultException ex) {
            return false;

        } finally {
            entityManager.close();
        }
    }


    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeTransformAudit c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id";

            Query query = entityManager.createQuery(sql, RdbmsExchangeTransformAudit.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString());

            List<RdbmsExchangeTransformAudit> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ExchangeTransformAudit(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
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

            return ret
                    .stream()
                    .map(T -> new ExchangeTransformAudit(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows, Date dateFrom, Date dateTo) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchange c"
                    + " where c.serviceId = :service_id"
                    + " and c.timestamp >= :date_from"
                    + " and c.timestamp <= :date_to"
                    + " order by c.timestamp desc";

            Query query = entityManager.createQuery(sql, RdbmsExchange.class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("date_from", dateFrom)
                    .setParameter("date_to", dateTo)
                    .setMaxResults(maxRows);

            List<RdbmsExchange> results = query.getResultList();

            List<Exchange> ret = new ArrayList<>();
            for (RdbmsExchange result : results) {
                ret.add(new Exchange(result));
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchange c"
                    + " where c.serviceId = :service_id"
                    + " order by c.timestamp desc";

            Query query = entityManager.createQuery(sql, RdbmsExchange.class)
                    .setParameter("service_id", serviceId.toString())
                    .setMaxResults(maxRows);

            List<RdbmsExchange> results = query.getResultList();

            List<Exchange> ret = new ArrayList<>();
            for (RdbmsExchange result : results) {
                ret.add(new Exchange(result));
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public List<ExchangeEvent> getExchangeEvents(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeEvent c"
                    + " where c.exchangeId = :exchange_id"
                    + " order by c.timestamp ASC";

            Query query = entityManager.createQuery(sql, RdbmsExchangeEvent.class)
                    .setParameter("exchange_id", exchangeId.toString());

            List<RdbmsExchangeEvent> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ExchangeEvent(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }



    public List<UUID> getExchangeIdsForService(UUID serviceId) throws Exception {

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c.id"
                    + " from"
                    + " RdbmsExchange c"
                    + " where c.serviceId = :service_id"
                    + " order by c.timestamp DESC";

            Query query = entityManager.createQuery(sql)
                    .setParameter("service_id", serviceId.toString());

            List<String> list = query.getResultList();

            List<UUID> ret = new ArrayList<>();
            for (String s : list) {
                UUID uuid = UUID.fromString(s);
                ret.add(uuid);
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    public ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, UUID id) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
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

            RdbmsExchangeTransformAudit result = (RdbmsExchangeTransformAudit)query.getSingleResult();
            return new ExchangeTransformAudit(result);

        } catch (NoResultException ex) {
            return null;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<ExchangeTransformAudit> getAllExchangeTransformAuditsForService(UUID serviceId, UUID systemId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c.exchangeId, c.numberBatchesCreated"
                    + " from"
                    + " RdbmsExchangeTransformAudit c"
                    + " where c.serviceId = :service_id"
                    + " and c.systemId = :system_id"
                    + " order by c.started desc";

            Query query = entityManager.createQuery(sql, Object[].class)
                    .setParameter("service_id", serviceId.toString())
                    .setParameter("system_id", systemId.toString());

            List<Object[]> results = query.getResultList();

            List<ExchangeTransformAudit> ret = new ArrayList<>();
            for (Object[] result : results) {
                String resultExchangeId = (String) result[0];
                Integer resultNumberBatches = (Integer) result[1];

                ExchangeTransformAudit audit = new ExchangeTransformAudit();
                audit.setExchangeId(UUID.fromString(resultExchangeId));
                audit.setNumberBatchesCreated(resultNumberBatches);
                ret.add(audit);
            }
            return ret;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public void save(ExchangeSubscriberTransformAudit subscriberTransformAudit) throws Exception {

        RdbmsExchangeSubscriberTransformAudit dbObj = new RdbmsExchangeSubscriberTransformAudit(subscriberTransformAudit);

        EntityManager entityManager = ConnectionManager.getAuditEntityManager();
        PreparedStatement ps = null;

        try {
            entityManager.getTransaction().begin();

            //have to use prepared statement as JPA doesn't support upserts
            //entityManager.persist(dbObj);

            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();

            String sql = "INSERT INTO exchange_subscriber_transform_audit"
                    + " (exchange_id, exchange_batch_id, subscriber_config_name, started, ended, error_xml, number_resources_transformed, queued_message_id)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " ended = VALUES(ended),"
                    + " error_xml = VALUES(error_xml),"
                    + " number_resources_transformed = VALUES(number_resources_transformed),"
                    + " queued_message_id = VALUES(queued_message_id);";

            ps = connection.prepareStatement(sql);

            ps.setString(1, dbObj.getExchangeId());
            ps.setString(2, dbObj.getExchangeBatchId());
            ps.setString(3, dbObj.getSubscriberConfigName());
            ps.setTimestamp(4, new java.sql.Timestamp(dbObj.getStarted().getTime()));
            if (dbObj.getEnded() != null) {
                ps.setTimestamp(5, new java.sql.Timestamp(dbObj.getEnded().getTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            if (!Strings.isNullOrEmpty(dbObj.getErrorXml())) {
                ps.setString(6, dbObj.getErrorXml());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }
            if (dbObj.getNumberResourcesTransformed() != null) {
                ps.setInt(7, dbObj.getNumberResourcesTransformed());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            if (!Strings.isNullOrEmpty(dbObj.getQueuedMessageId())) {
                ps.setString(8, dbObj.getQueuedMessageId());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } finally {
            entityManager.close();
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeSubscriberTransformAudit c"
                    + " where c.exchangeId = :exchange_id"
                    + " order by c.started";

            Query query = entityManager.createQuery(sql, RdbmsExchangeSubscriberTransformAudit.class)
                    .setParameter("exchange_id", exchangeId.toString());

            List<RdbmsExchangeSubscriberTransformAudit> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ExchangeSubscriberTransformAudit(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId, UUID exchangeBatchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getAuditEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsExchangeSubscriberTransformAudit c"
                    + " where c.exchangeId = :exchange_id"
                    + " and c.exchangeBatchId = :exchange_batch_id"
                    + " order by c.started";

            Query query = entityManager.createQuery(sql, RdbmsExchangeSubscriberTransformAudit.class)
                    .setParameter("exchange_id", exchangeId.toString())
                    .setParameter("exchange_batch_id", exchangeBatchId.toString());

            List<RdbmsExchangeSubscriberTransformAudit> ret = query.getResultList();

            return ret
                    .stream()
                    .map(T -> new ExchangeSubscriberTransformAudit(T))
                    .collect(Collectors.toList());

        } finally {
            entityManager.close();
        }
    }

}
