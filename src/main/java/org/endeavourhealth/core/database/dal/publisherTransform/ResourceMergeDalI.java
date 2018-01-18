package org.endeavourhealth.core.database.dal.publisherTransform;

import java.util.UUID;

public interface ResourceMergeDalI {

    void recordMerge(UUID serviceId, UUID resourceFrom, UUID resourceTo) throws Exception;

    UUID ResolveMergeUUID(UUID serviceId, UUID resourceId) throws Exception;
}
