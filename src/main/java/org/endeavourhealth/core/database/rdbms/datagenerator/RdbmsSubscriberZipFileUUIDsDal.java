package org.endeavourhealth.core.database.rdbms.datagenerator;

import org.endeavourhealth.core.database.dal.datagenerator.SubscriberZipFileUUIDsDalI;
import org.endeavourhealth.core.database.dal.datagenerator.models.RemoteFilingStatistics;
import org.endeavourhealth.core.database.dal.datagenerator.models.RemoteFilingSubscriber;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datagenerator.models.RdbmsSubscriberZipFileUUIDs;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RdbmsSubscriberZipFileUUIDsDal implements SubscriberZipFileUUIDsDalI {

    public RdbmsSubscriberZipFileUUIDs getSubscriberZipFileUUIDsEntity(String queuedMessageUUID) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            RdbmsSubscriberZipFileUUIDs ret = entityManager.find(RdbmsSubscriberZipFileUUIDs.class, queuedMessageUUID);
            return ret;

        } catch (Exception ex){
            return null;

        } finally {
            entityManager.close();
        }

    }

    public List<RdbmsSubscriberZipFileUUIDs> getPagedSubscriberZipFileUUIDsEntities(Integer pageNumber, Integer pageSize) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RdbmsSubscriberZipFileUUIDs> cq = cb.createQuery(RdbmsSubscriberZipFileUUIDs.class);
            Root<RdbmsSubscriberZipFileUUIDs> rootEntry = cq.from(RdbmsSubscriberZipFileUUIDs.class);
            CriteriaQuery<RdbmsSubscriberZipFileUUIDs> all = cq.select(rootEntry);
            cq.orderBy(cb.asc(rootEntry.get("filingOrder")));
            TypedQuery<RdbmsSubscriberZipFileUUIDs> allQuery = entityManager.createQuery(all);

            allQuery.setFirstResult((pageNumber - 1) * pageSize);
            allQuery.setMaxResults(pageSize);

            List<RdbmsSubscriberZipFileUUIDs> ret = allQuery.getResultList();
            return ret;

        } catch (Exception ex) {
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public List<RdbmsSubscriberZipFileUUIDs> getPagedSubscriberZipFileUUIDsEntities(Integer subscriberId, Integer pageNumber, Integer pageSize) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<RdbmsSubscriberZipFileUUIDs> cq = cb.createQuery(RdbmsSubscriberZipFileUUIDs.class);
            Root<RdbmsSubscriberZipFileUUIDs> rootEntry = cq.from(RdbmsSubscriberZipFileUUIDs.class);
            CriteriaQuery<RdbmsSubscriberZipFileUUIDs> all = cq.select(rootEntry);
            cq.where(cb.equal(rootEntry.get("subscriberId"), subscriberId));
            cq.orderBy(cb.asc(rootEntry.get("filingOrder")));
            TypedQuery<RdbmsSubscriberZipFileUUIDs> allQuery = entityManager.createQuery(all);

            allQuery.setFirstResult((pageNumber - 1) * pageSize);
            allQuery.setMaxResults(pageSize);

            List<RdbmsSubscriberZipFileUUIDs> ret = allQuery.getResultList();
            return ret;

        } catch (Exception ex) {
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public Long getTotalNumberOfSubscriberFiles() throws Exception {
        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<RdbmsSubscriberZipFileUUIDs> rootEntry = cq.from(RdbmsSubscriberZipFileUUIDs.class);

        cq.select((cb.countDistinct(rootEntry)));

        Long ret = entityManager.createQuery(cq).getSingleResult();

        entityManager.close();

        return ret;
    }

    public Long getTotalNumberOfSubscriberFiles(Integer subscriberId) throws Exception {
        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<RdbmsSubscriberZipFileUUIDs> rootEntry = cq.from(RdbmsSubscriberZipFileUUIDs.class);
        cq.where(cb.equal(rootEntry.get("subscriberId"), subscriberId));
        cq.select((cb.countDistinct(rootEntry)));

        Long ret = entityManager.createQuery(cq).getSingleResult();

        entityManager.close();

        return ret;
    }

    public synchronized RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(RdbmsSubscriberZipFileUUIDs rszfu) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {

            String sql = "select max(filing_order) from data_generator.subscriber_zip_file_uuids;";
            Query query = entityManager.createNativeQuery(sql);
            BigInteger bigResult = (BigInteger) query.getSingleResult();
            Long longResult = bigResult.longValue();

            if (longResult == null) {
                rszfu.setFilingOrder(1);
            } else {
                rszfu.setFilingOrder(longResult + 1);
            }

            entityManager.getTransaction().begin();
            entityManager.persist(rszfu);
            entityManager.getTransaction().commit();
            return rszfu;

        } catch (Exception ex) {

            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    public synchronized RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(int subscriberId, String batchId,
                                                                                       String queuedMessageId,
                                                                                       String queuedMessageBody) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            RdbmsSubscriberZipFileUUIDs rszfu = new RdbmsSubscriberZipFileUUIDs();

            rszfu.setSubscriberId(subscriberId);
            rszfu.setBatchUUID(batchId);
            rszfu.setQueuedMessageUUID(queuedMessageId);
            rszfu.setQueuedMessageBody(queuedMessageBody);

            String sql = "select max(filing_order) from data_generator.subscriber_zip_file_uuids;";
            Query query = entityManager.createNativeQuery(sql);
            BigInteger bigResult = (BigInteger) query.getSingleResult();

            if (bigResult == null) {
                rszfu.setFilingOrder(1);
            } else {
                Long longResult = bigResult.longValue();
                if (longResult == null) {
                    rszfu.setFilingOrder(1);
                } else {
                    rszfu.setFilingOrder(longResult + 1);
                }
            }

            entityManager.getTransaction().begin();
            entityManager.persist(rszfu);
            entityManager.getTransaction().commit();
            return rszfu;

        } catch (Exception ex) {

            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<RemoteFilingSubscriber> getSubscribers() throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();
        PreparedStatement ps = null;
        try {
            SessionImpl session = (SessionImpl) entityManager.getDelegate();
            Connection connection = session.connection();
            String sql = "select subscriber_id, subscriber_live, definition "
                    + "from subscriber_file_sender ";

            ps = connection.prepareStatement(sql);

            List<RemoteFilingSubscriber> subscribers = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int col = 1;

                RemoteFilingSubscriber subscriber = new RemoteFilingSubscriber();
                subscriber.setId(rs.getInt(col++));
                subscriber.setIsLive(rs.getBoolean(col++));
                subscriber.setJsonDefinition(rs.getString(col++));

                subscribers.add(subscriber);
            }

            return subscribers;

        } finally {
            if (ps != null) {
                ps.close();
            }
            entityManager.close();
        }
    }

    @Override
    public List<RemoteFilingStatistics> getSubscriberStatistics(Integer subscriberId, String timeFrame) throws Exception {

        List<RemoteFilingStatistics> stats = new ArrayList<>();

        stats.add(getUnSentStats(timeFrame, subscriberId));
        stats.add(getSentStats(timeFrame, subscriberId));
        stats.add(getAwaitingProcessingStats(timeFrame, subscriberId));
        stats.add(getSuccessfullyFiledStats(timeFrame, subscriberId));
        stats.add(getFailedFilingStats(timeFrame, subscriberId));

        return stats;
    }

    @Override
    public List<RemoteFilingStatistics> getStatistics(String timeFrame) throws Exception {
        List<RemoteFilingStatistics> stats = new ArrayList<>();
        stats.add(getUnSentStats(timeFrame, null));
        stats.add(getSentStats(timeFrame, null));
        stats.add(getAwaitingProcessingStats(timeFrame, null));
        stats.add(getSuccessfullyFiledStats(timeFrame, null));
        stats.add(getFailedFilingStats(timeFrame, null));

        return stats;
    }

    private RemoteFilingStatistics getUnSentStats(String timeFrame, Integer subscriberId) throws Exception {
        String sql = "select 'Files awaiting sending (total)',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent is null";
        if (subscriberId != null) {
            sql = sql +" and s.subscriberId = :subscriberId";
        }

        return executeSQL(sql, timeFrame, true, subscriberId);
    }

    private RemoteFilingStatistics getSentStats(String timeFrame, Integer subscriberId) throws Exception {
        String sql = "select 'Files sent (time frame)',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date";
        if (subscriberId != null) {
            sql = sql +" and s.subscriberId = :subscriberId ";
        }
        return executeSQL(sql, timeFrame, false, subscriberId);
    }

    private RemoteFilingStatistics getAwaitingProcessingStats(String timeFrame, Integer subscriberId) throws Exception {
        String sql = "select ' - Awaiting processing',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingAttempted is null";
        if (subscriberId != null) {
            sql = sql +" and s.subscriberId = :subscriberId ";
        }

        return executeSQL(sql, timeFrame, false, subscriberId);
    }

    private RemoteFilingStatistics getSuccessfullyFiledStats(String timeFrame, Integer subscriberId) throws Exception {
        String sql = "select ' - Successfully filed',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingSuccess = 1";
        if (subscriberId != null) {
            sql = sql +" and s.subscriberId = :subscriberId ";
        }

        return executeSQL(sql, timeFrame, false, subscriberId);
    }

    private RemoteFilingStatistics getFailedFilingStats(String timeFrame, Integer subscriberId) throws Exception {
        String sql = "select ' - Filing errors',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingSuccess = 0";
        if (subscriberId != null) {
            sql = sql +" and s.subscriberId = :subscriberId ";
        }

        return executeSQL(sql, timeFrame, false, subscriberId);
    }

    private RemoteFilingStatistics executeSQL(String sql, String timeFrame, Boolean ignoreDate, Integer subscriberId) throws Exception {
        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        Calendar cal = Calendar.getInstance();
        if (timeFrame.equals("day")) {
            cal.add(Calendar.HOUR, -24);
        } else if (timeFrame.equals("month")) {
            cal.add(Calendar.MONTH, -1);
        } else {
            cal.add(Calendar.YEAR, -1);
        }
        Date date = cal.getTime();

        Query query = entityManager.createQuery(sql);

        if (!ignoreDate) {
            query.setParameter("date", date);
        }

        if (subscriberId != null) {
            query.setParameter("subscriberId", subscriberId);
        }

        try {
            List<Object[]> result = query.getResultList();
            RemoteFilingStatistics stats = new RemoteFilingStatistics();
            stats.setStatisticsText(result.get(0)[0].toString());
            stats.setStatisticsValue(result.get(0)[1].toString());
            stats.setSubscriberId(subscriberId);

            return stats;

        } catch (NoResultException ex) {
            return null;

        }
    }

    public List<RdbmsSubscriberZipFileUUIDs> getFailedFilingUUIDEntities(String timeFrame, Integer subscriberId) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            String sql = "select s"
                    + " from"
                    + " RdbmsSubscriberZipFileUUIDs s"
                    + " where s.fileSent >= :date"
                    + " and s.fileFilingSuccess = 0"
                    + " and s.subscriberId = :subscriberId "
                    + " order by s.filingOrder asc ";

            Calendar cal = Calendar.getInstance();
            if (timeFrame.equals("day")) {
                cal.add(Calendar.HOUR, -24);
            } else if (timeFrame.equals("month")) {
                cal.add(Calendar.MONTH, -1);
            } else {
                cal.add(Calendar.YEAR, -1);
            }
            Date date = cal.getTime();

            Query query = entityManager.createQuery(sql);
            query.setParameter("date", date);
            query.setParameter("subscriberId", subscriberId);

            List<RdbmsSubscriberZipFileUUIDs> ret = query.getResultList();
            return ret;

        } catch (Exception ex) {
            throw ex;

        } finally {
            entityManager.close();
        }
    }
}