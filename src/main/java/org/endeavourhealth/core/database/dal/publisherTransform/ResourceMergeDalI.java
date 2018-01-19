package org.endeavourhealth.core.database.dal.publisherTransform;

import java.util.UUID;

public interface ResourceMergeDalI {

    void recordMerge(UUID serviceId, String resourceType, UUID resourceFrom, UUID resourceTo) throws Exception;

    UUID ResolveMergeUUID(UUID serviceId, String resourceType, UUID resourceId) throws Exception;
}
