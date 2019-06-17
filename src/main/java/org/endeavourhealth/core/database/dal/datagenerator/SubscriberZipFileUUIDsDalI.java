package org.endeavourhealth.core.database.dal.datagenerator;

import org.endeavourhealth.core.database.dal.datagenerator.models.RemoteFilingStatistics;
import org.endeavourhealth.core.database.rdbms.datagenerator.models.RdbmsSubscriberZipFileUUIDs;

import java.util.List;

public interface SubscriberZipFileUUIDsDalI {

    RdbmsSubscriberZipFileUUIDs getSubscriberZipFileUUIDsEntity(String queuedMessageUUID) throws Exception;

    List<RdbmsSubscriberZipFileUUIDs> getPagedSubscriberZipFileUUIDsEntities(Integer pageNumber, Integer pageSize) throws Exception;

    Long getTotalNumberOfSubscriberFiles() throws Exception;

    RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(RdbmsSubscriberZipFileUUIDs rszfu) throws Exception;

    RdbmsSubscriberZipFileUUIDs createSubscriberZipFileUUIDsEntity(int subscriberId, String batchId, String queuedMessageId,
                                                                   String queuedMessageBody) throws Exception;

    List<RemoteFilingStatistics> getStatistics(String timeframe) throws Exception;
}
