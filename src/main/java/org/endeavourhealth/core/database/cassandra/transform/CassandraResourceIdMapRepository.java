package org.endeavourhealth.core.database.cassandra.transform;

import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.transform.accessors.ResourceIdMapAccessor;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMap;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMapByEdsId;
import org.endeavourhealth.core.database.dal.transform.ResourceIdTransformDalI;
import org.endeavourhealth.core.database.dal.transform.models.ResourceIdMap;

import java.util.Iterator;
import java.util.UUID;

public class CassandraResourceIdMapRepository extends Repository implements ResourceIdTransformDalI {

    public void insert(ResourceIdMap resourceIdMap) {
        if (resourceIdMap == null) {
            throw new IllegalArgumentException("resourceIdMap is null");
        }

        CassandraResourceIdMap dbObj = new CassandraResourceIdMap(resourceIdMap);

        Mapper<CassandraResourceIdMap> mapper = getMappingManager().mapper(CassandraResourceIdMap.class);
        mapper.save(dbObj);
    }

    public ResourceIdMap getResourceIdMap(UUID serviceId, UUID systemId, String resourceType, String sourceId) {

        ResourceIdMapAccessor accessor = getMappingManager().createAccessor(ResourceIdMapAccessor.class);
        Iterator<CassandraResourceIdMap> iterator = accessor.getResourceIdMap(serviceId, systemId, resourceType, sourceId).iterator();
        if (iterator.hasNext()) {
            CassandraResourceIdMap result = iterator.next();
            return new ResourceIdMap(result);
        } else {
            return null;
        }
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, String edsId) {
        return getResourceIdMapByEdsId(resourceType, UUID.fromString(edsId));
    }

    public ResourceIdMap getResourceIdMapByEdsId(String resourceType, UUID edsId) {

        ResourceIdMapAccessor accessor = getMappingManager().createAccessor(ResourceIdMapAccessor.class);
        Iterator<CassandraResourceIdMapByEdsId> iterator = accessor.getResourceIdMapByEdsId(resourceType, edsId).iterator();
        if (iterator.hasNext()) {
            CassandraResourceIdMapByEdsId result = iterator.next();
            return new ResourceIdMap(result);
        } else {
            return null;

        }
    }
}
