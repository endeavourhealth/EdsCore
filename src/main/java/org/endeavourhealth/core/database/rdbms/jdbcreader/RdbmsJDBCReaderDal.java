package org.endeavourhealth.core.database.rdbms.jdbcreader;

import org.endeavourhealth.core.database.dal.jdbcreader.JDBCReaderDalI;
import org.endeavourhealth.core.database.dal.jdbcreader.models.*;
import org.endeavourhealth.core.database.rdbms.ConnectionManager;
import org.endeavourhealth.core.database.rdbms.jdbcreader.models.*;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RdbmsJDBCReaderDal implements JDBCReaderDalI {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsJDBCReaderDal.class);

    @Override
    public void insertBatch(Batch batch) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            RdbmsBatch save = new RdbmsBatch(batch);
            //save.setBatchId(null);
            entityManager.getTransaction().begin();
            entityManager.persist(save);
            entityManager.getTransaction().commit();
            batch.setBatchId(save.getBatchId());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void insertBatchFile(BatchFile batchFile) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            RdbmsBatchFile save = new RdbmsBatchFile(batchFile);
            entityManager.getTransaction().begin();
            entityManager.persist(save);
            entityManager.getTransaction().commit();
            batchFile.setBatchFileId(save.getBatchFileId());

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void insertNotificationMessage(NotificationMessage notificationMessage) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            RdbmsNotificationMessage save = new RdbmsNotificationMessage(notificationMessage);
            entityManager.getTransaction().begin();
            entityManager.persist(save);
            entityManager.getTransaction().commit();
            notificationMessage.setNotificationMessageId(save.getNotificationMessageId());
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void insertUpdateKeyValuePair(KeyValuePair kvp) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        Connection connection = session.connection();
        try {

            String sql = "INSERT INTO key_value_pairs"
                    + " (batch_name, connection_name, key_value, data_value)"
                    + " VALUES (?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE"
                    + " batch_name = VALUES(batch_name),"
                    + " connection_name = VALUES(connection_name),"
                    + " key_value = VALUES(key_value),"
                    + " data_value = VALUES(data_value);";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, kvp.getBatchName());
            ps.setString(2, kvp.getConnectionName());
            ps.setString(3, kvp.getKeyValue());
            ps.setString(4, kvp.getDataValue());

            entityManager.getTransaction().begin();

            ps.executeUpdate();

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void updateBatchFile(BatchFile batchFile) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            entityManager.getTransaction().begin();
            RdbmsBatchFile update = entityManager.find(RdbmsBatchFile.class, batchFile.getBatchFileId());
            update.setDownloaded(batchFile.isDownloaded());
            update.setDownloadDate(batchFile.getDownloadDate());
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<Batch> getIncompleteBatches(String configurationId, String partialBatchIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatch c"
                    + " where c.configurationId= :configuration_id"
                    + " and c.batchIdentifier LIKE :batch_identifier"
                    + " and c.complete = false";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("configuration_id", configurationId)
                    .setParameter("batch_identifier", partialBatchIdentifier + "#%");

            List<RdbmsBatch> batchList = query.getResultList();

            List<Batch> ret = batchList
                    .stream()
                    .map(T -> new Batch(T))
                    .collect(Collectors.toList());

            for (Batch batch: ret) {
                List<BatchFile> fileList = getFilesInBatch(batch, entityManager);
                batch.setBatchFiles(fileList);
            }

            return ret;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private List<BatchFile> getFilesInBatch(Batch batch, EntityManager entityManager) {
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatchFile c"
                    + " where c.batchId = :batch_id";

            Query query = entityManager.createQuery(sql, RdbmsBatchFile.class)
                    .setParameter("batch_id", batch.getBatchId());

            List<RdbmsBatchFile> ret = query.getResultList();

            return ret.stream()
                    .map(T -> new BatchFile(T))
                    .collect(Collectors.toList());

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<Instance> getInstances() throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsInstance c";

            Query query = entityManager.createQuery(sql, RdbmsInstance.class);

            List<RdbmsInstance> ret = query.getResultList();

            return ret.stream()
                    .map(T -> new Instance(T))
                    .collect(Collectors.toList());

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<KeyValuePair> getKeyValuePairs(String batchName, String connectionName) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsKeyValuePair c"
                    + " where c.batchName = :batch_name"
                    + " and c.connectionName = :connection_name";

            Query query = entityManager.createQuery(sql, RdbmsKeyValuePair.class)
                    .setParameter("batch_name", batchName)
                    .setParameter("connection_name", connectionName);

            List<RdbmsKeyValuePair> ret = query.getResultList();

            return ret.stream()
                    .map(T -> new KeyValuePair(T))
                    .collect(Collectors.toList());

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void updateBatch(Batch batch) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            entityManager.getTransaction().begin();
            RdbmsBatch update = entityManager.find(RdbmsBatch.class, batch.getBatchId());
            update.setLocalPath(batch.getLocalPath());
            update.setComplete(batch.isComplete());
            update.setCompleteDate(batch.getCompleteDate());
            update.setNotified(batch.isNotified());
            update.setNotificationDate(batch.getNotificationDate());
            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public void setBatchAsComplete(Batch batch) throws Exception {
        batch.setComplete(true);
        batch.setCompleteDate(new Date());
        updateBatch(batch);
    }

    @Override
    public void setBatchFileAsDownloaded(BatchFile batchFile) throws Exception {
        batchFile.setDownloaded(true);
        batchFile.setDownloadDate(new Date());
        updateBatchFile(batchFile);
    }

    @Override
    public void setBatchAsNotified(Batch batch) throws Exception {
        batch.setNotified(true);
        batch.setNotificationDate(new Date());
        updateBatch(batch);
    }

    @Override
    public List<Batch> getUnnotifiedBatches(String configurationId, String partialBatchIdentifier) throws Exception {
        List<Batch> ret = new ArrayList<Batch>();
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatch c"
                    + " where c.configurationId = :configuration_id"
                    + " and c.batchIdentifier LIKE :batch_identifier"
                    + " and c.complete = true"
                    + " and c.notified = false"
                    + " order by c.batchId asc";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("configuration_id", configurationId)
                    .setParameter("batch_identifier", partialBatchIdentifier + "#%");

            List<RdbmsBatch> rdbmsBatchList = query.getResultList();

            for (RdbmsBatch bs : rdbmsBatchList) {
                Batch batch = new Batch(bs);
                ret.add(batch);
            }

            return ret;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public Batch getLastCompleteBatch(String configurationId, String partialBatchIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();

        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatch c"
                    + " where c.configurationId= :configuration_id"
                    + " and c.batchIdentifier LIKE :batch_identifier"
                    + " and c.complete = true"
                    + " order by c.batchId desc";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("configuration_id", configurationId)
                    .setParameter("batch_identifier", partialBatchIdentifier + "#%");

            List<RdbmsBatch> ret = query.getResultList();

            if (ret.size() >= 1) {
                return new Batch(ret.get(0));
            } else {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public Batch getLastBatch(String configurationId, String partialBatchIdentifier) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatch c"
                    + " where c.configurationId = :configuration_id"
                    + " and c.batchIdentifier LIKE :batch_identifier"
                    + " order by c.batchId desc";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("configuration_id", configurationId)
                    .setParameter("batch_identifier", partialBatchIdentifier + "#%")
                    .setMaxResults(1);

            try {
                RdbmsBatch batch = (RdbmsBatch) query.getSingleResult();
                Batch ret = new Batch(batch);
                ret.setBatchFiles(getFilesInBatch(ret, entityManager));
                return ret;
            }
            catch (NoResultException e) {
                LOG.trace("No last batch found");
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public Batch getBatch(Long batchId) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatch c"
                    + " where c.batchId= :batch_id";

            Query query = entityManager.createQuery(sql, RdbmsBatch.class)
                    .setParameter("batch_id", batchId);

            try {
                RdbmsBatch batch = (RdbmsBatch) query.getSingleResult();
                Batch ret = new Batch(batch);
                ret.setBatchFiles(getFilesInBatch(ret, entityManager));
                return ret;
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public BatchFile getBatchFile(Long batchId, String fileName) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsBatchFile c"
                    + " where c.batchId= :batch_id"
                    + " and c.filename = :file_name";

            Query query = entityManager.createQuery(sql, RdbmsBatchFile.class)
                    .setParameter("batch_id", batchId)
                    .setParameter("file_name", fileName);

            try {
                RdbmsBatchFile batchFile = (RdbmsBatchFile) query.getSingleResult();
                return new BatchFile(batchFile);
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Override
    public KeyValuePair getKeyValuePair(KeyValuePair kvp) throws Exception {
        EntityManager entityManager = ConnectionManager.getJDBCReaderEntityManager();
        try {
            String sql = "select c"
                    + " from"
                    + " RdbmsKeyValuePair c"
                    + " where c.batchName = :batch_name"
                    + " and c.connectionName = :connection_name"
                    + " and c.keyValue = :key_value";

            Query query = entityManager.createQuery(sql, RdbmsKeyValuePair.class)
                    .setParameter("batch_name", kvp.getBatchName())
                    .setParameter("connection_name", kvp.getConnectionName())
                    .setParameter("key_value", kvp.getKeyValue());

            try {
                RdbmsKeyValuePair rdbmsKVP = (RdbmsKeyValuePair) query.getSingleResult();
                return new KeyValuePair(rdbmsKVP);
            }
            catch (NoResultException e) {
                return null;
            }
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

}
