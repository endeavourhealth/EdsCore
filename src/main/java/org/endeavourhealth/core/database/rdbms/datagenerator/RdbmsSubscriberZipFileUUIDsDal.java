package org.endeavourhealth.core.database.rdbms.datagenerator;

import org.endeavourhealth.core.database.dal.datagenerator.SubscriberZipFileUUIDsDalI;
import org.endeavourhealth.core.database.dal.datagenerator.models.RemoteFilingStatistics;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.datagenerator.models.RdbmsSubscriberZipFileUUIDs;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
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

    public RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(int subscriberId, String queuedMessageId,
                                                                          String queuedMessageBody) throws Exception {

        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        try {
            RdbmsSubscriberZipFileUUIDs rszfu = new RdbmsSubscriberZipFileUUIDs();

            rszfu.setSubscriberId(subscriberId);
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
    public List<RemoteFilingStatistics> getStatistics(String timeframe) throws Exception {
        List<RemoteFilingStatistics> stats = new ArrayList<>();
        stats.add(getUnSentStats(timeframe));
        stats.add(getSentStats(timeframe));
        stats.add(getAwaitingProcessingStats(timeframe));
        stats.add(getSuccessfullyFiledStats(timeframe));
        stats.add(getFailedFilingStats(timeframe));

        return stats;
    }

    private RemoteFilingStatistics getUnSentStats(String timeframe) throws Exception {
        String sql = "select 'Files awaiting sending',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent is null";

        return executeSQL(sql, timeframe, true);
    }

    private RemoteFilingStatistics getSentStats(String timeframe) throws Exception {
        String sql = "select 'Files sent',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date";

        return executeSQL(sql, timeframe, false);
    }

    private RemoteFilingStatistics getAwaitingProcessingStats(String timeframe) throws Exception {
        String sql = "select 'Awaiting processing',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingAttempted is null";

        return executeSQL(sql, timeframe, false);
    }

    private RemoteFilingStatistics getSuccessfullyFiledStats(String timeframe) throws Exception {
        String sql = "select 'Successfully filed',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingSuccess = 1";

        return executeSQL(sql, timeframe, false);
    }

    private RemoteFilingStatistics getFailedFilingStats(String timeframe) throws Exception {
        String sql = "select 'Failed filing',  count(s)"
                + " from"
                + " RdbmsSubscriberZipFileUUIDs s"
                + " where s.fileSent >= :date"
                + " and s.fileFilingSuccess = 0";

        return executeSQL(sql, timeframe, false);
    }

    private RemoteFilingStatistics executeSQL(String sql, String timeframe, Boolean ignoreDate) throws Exception {
        EntityManager entityManager = ConnectionManager.getDataGeneratorEntityManager();

        Calendar cal = Calendar.getInstance();
        if (timeframe.equals("day")) {
            cal.add(Calendar.HOUR, -24);
        } else if (timeframe.equals("month")) {
            cal.add(Calendar.MONTH, -1);
        } else {
            cal.add(Calendar.YEAR, -1);
        }
        Date date = cal.getTime();

        Query query = entityManager.createQuery(sql);

        if (!ignoreDate) {
            query.setParameter("date", date);
        }

        try {
            List<Object[]> result = query.getResultList();
            RemoteFilingStatistics stats = new RemoteFilingStatistics();
            stats.setStatisticsText(result.get(0)[0].toString());
            stats.setStatisticsValue(result.get(0)[1].toString());

            return stats;

        } catch (NoResultException ex) {
            return null;

        }
    }

}
