package org.endeavourhealth.core.database.dal.jdbcreader;

import org.endeavourhealth.core.database.dal.jdbcreader.models.Batch;
import org.endeavourhealth.core.database.dal.jdbcreader.models.BatchFile;
import org.endeavourhealth.core.database.dal.jdbcreader.models.Instance;
import org.endeavourhealth.core.database.dal.jdbcreader.models.KeyValuePair;
import org.endeavourhealth.core.database.dal.jdbcreader.models.NotificationMessage;

import java.util.List;

public interface JDBCReaderDalI {

    public void insertBatch(Batch batch) throws Exception;
    public void insertBatchFile(BatchFile batchFile) throws Exception;
    public void insertNotificationMessage(NotificationMessage notificationMessage) throws Exception;
    public void insertUpdateKeyValuePair(KeyValuePair kvp) throws Exception;

    public Batch getBatch(Long batchId) throws Exception;
    public BatchFile getBatchFile(Long batchId, String fileName) throws Exception;
    public KeyValuePair getKeyValuePair(KeyValuePair kvp) throws Exception;
    public List<Instance> getInstances() throws Exception;
    public List<KeyValuePair> getKeyValuePairs(String batchName, String connectionName) throws Exception;

    public void updateBatch(Batch batch) throws Exception;
    public void updateBatchFile(BatchFile batchFile) throws Exception;

    public Batch getLastCompleteBatch(String configurationId, String partialBatchIdentifier) throws Exception;
    public Batch getLastBatch(String configurationId, String partialBatchIdentifier) throws Exception;
    public List<Batch> getIncompleteBatches(String configurationId, String partialBatchIdentifier) throws Exception;
    public void setBatchAsComplete(Batch batch) throws Exception;
    public void setBatchAsNotified(Batch batch) throws Exception;
    public void setBatchFileAsDownloaded(BatchFile batchFile) throws Exception;
    public List<Batch> getUnnotifiedBatches(String configurationId, String partialBatchIdentifier) throws Exception;
}
