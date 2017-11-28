package org.endeavourhealth.core.database.cassandra.transform;

import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.common.fhir.ReferenceComponents;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.core.database.cassandra.transform.accessors.ResourceIdMapAccessor;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMap;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraResourceIdMapByEdsId;
import org.endeavourhealth.core.database.dal.publisherTransform.ResourceIdTransformDalI;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CassandraResourceIdMapRepository extends Repository implements ResourceIdTransformDalI {

    private static final Map<String, AtomicInteger> synchLocks = new HashMap<>();


    @Override
    public UUID findOrCreateThreadSafe(UUID serviceId, UUID systemId, String resourceType, String sourceId) throws Exception {

        String cacheKey = resourceType + "\\" + sourceId;

        //we need to synch to prevent two threads generating an ID for the same source ID at the same time
        //use an AtomicInt for each cache key as a synchronisation object and as a way to track
        AtomicInteger atomicInteger = null;
        synchronized (synchLocks) {
            atomicInteger = synchLocks.get(cacheKey);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
                synchLocks.put(cacheKey, atomicInteger);
            }

            atomicInteger.incrementAndGet();
        }

        UUID ret = null;

        synchronized (atomicInteger) {

            //now we're safely locked, we can check Cassandra for the mapping again
            ResourceIdMapAccessor accessor = getMappingManager().createAccessor(ResourceIdMapAccessor.class);
            Iterator<CassandraResourceIdMap> iterator = accessor.getResourceIdMap(serviceId, systemId, resourceType, sourceId).iterator();
            if (iterator.hasNext()) {
                CassandraResourceIdMap result = iterator.next();
                ret = result.getEdsId();

            } else {
                //if no mapping can still be found, then create and save a new mapping
                ret = UUID.randomUUID();

                CassandraResourceIdMap mapping = new CassandraResourceIdMap();
                mapping.setServiceId(serviceId);
                mapping.setSystemId(systemId);
                mapping.setResourceType(resourceType);
                mapping.setSourceId(sourceId);
                mapping.setEdsId(ret);

                Mapper<CassandraResourceIdMap> mapper = getMappingManager().mapper(CassandraResourceIdMap.class);
                mapper.save(mapping);
            }
        }

        synchronized (synchLocks) {
            int val = atomicInteger.decrementAndGet();
            if (val == 0) {
                synchLocks.remove(cacheKey);
            }
        }

        return ret;
    }

    @Override
    public Map<Reference, Reference> findEdsReferencesFromSourceReferences(UUID serviceId, UUID systemId, List<Reference> sourceReferences) throws Exception {
        Map<Reference, Reference> ret = new HashMap<>();

        ResourceIdMapAccessor accessor = getMappingManager().createAccessor(ResourceIdMapAccessor.class);

        for (Reference sourceReference: sourceReferences) {
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(sourceReference);
            String resourceType = comps.getResourceType().toString();
            String sourceId = comps.getId();

            Iterator<CassandraResourceIdMap> iterator = accessor.getResourceIdMap(serviceId, systemId, resourceType, sourceId).iterator();
            if (iterator.hasNext()) {
                CassandraResourceIdMap result = iterator.next();
                UUID edsId = result.getEdsId();
                Reference edsReference = ReferenceHelper.createReference(resourceType, edsId.toString());
                ret.put(sourceReference, edsReference);
            }
        }

        return ret;
    }

    @Override
    public Map<Reference, Reference> findSourceReferencesFromEdsReferences(List<Reference> edsReferences) throws Exception {
        //note this is really inefficient, hitting the DB for each entry, but it's a quick implementation
        //to support this new interface function. It's properly implemented in the MySQL class, doing all in one DB call
        Map<Reference, Reference> ret = new HashMap<>();

        ResourceIdMapAccessor accessor = getMappingManager().createAccessor(ResourceIdMapAccessor.class);

        for (Reference edsReference: edsReferences) {
            ReferenceComponents comps = ReferenceHelper.getReferenceComponents(edsReference);
            ResourceType resourceType = comps.getResourceType();
            UUID edsId = UUID.fromString(comps.getId());

            Iterator<CassandraResourceIdMapByEdsId> iterator = accessor.getResourceIdMapByEdsId(resourceType.toString(), edsId).iterator();
            if (iterator.hasNext()) {
                CassandraResourceIdMapByEdsId result = iterator.next();
                String sourceId = result.getSourceId();
                Reference sourceReference = ReferenceHelper.createReference(resourceType, sourceId);
                ret.put(edsReference, sourceReference);
            }
        }

        return ret;
    }
}
