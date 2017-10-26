package org.endeavourhealth.core.database.dal.publisherTransform;

import org.endeavourhealth.core.database.dal.publisherTransform.models.ResourceIdMap;

import java.util.UUID;

public interface ResourceIdTransformDalI {

    public void insert(ResourceIdMap resourceIdMap) throws Exception;
    public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception;

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) throws Exception;
    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) throws Exception;

}
