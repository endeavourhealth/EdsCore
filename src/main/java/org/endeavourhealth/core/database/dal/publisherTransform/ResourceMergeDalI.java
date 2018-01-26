package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceMergeMap;
import org.endeavourhealth.core.database.rdbms.publisherTransform.models.RdbmsResourceMergeMap;

import java.util.UUID;

public interface ResourceMergeDalI {

    void insertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception;

    void updateMergeRecord(RdbmsResourceMergeMap dbObj) throws Exception;

    void upsertMergeRecord(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception;

    ResourceMergeMap readMergeRecord(UUID serviceId, String resourceType, UUID resourceId) throws Exception;

    UUID resolveMergeUUID(UUID serviceId, String resourceType, UUID resourceId) throws Exception;

    String resolveMerge(String serviceId, String resourceType, String resourceId) throws Exception;
}
