package org.endeavourhealth.core.database.cassandra.transform;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.transform.accessors.EmisAccessor;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraEmisAdminResourceCache;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraEmisCsvCodeMap;
import org.endeavourhealth.core.database.dal.publisherCommon.EmisTransformDalI;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisAdminResourceCache;
import org.endeavourhealth.core.database.dal.publisherCommon.models.EmisCsvCodeMap;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CassandraEmisRepository extends Repository implements EmisTransformDalI {

    public void save(EmisCsvCodeMap mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        CassandraEmisCsvCodeMap cassandraMapping = new CassandraEmisCsvCodeMap(mapping);
        cassandraMapping.setTimeUuid(UUIDs.timeBased()); //this isn't part of the proxy, so needs generating here

        Mapper<CassandraEmisCsvCodeMap> mapper = getMappingManager().mapper(CassandraEmisCsvCodeMap.class);
        mapper.save(cassandraMapping);
    }

    public EmisCsvCodeMap getMostRecentCode(String dataSharingAgreementGuid, boolean medication, Long codeId) {

        EmisAccessor accessor = getMappingManager().createAccessor(EmisAccessor.class);
        Iterator<CassandraEmisCsvCodeMap> iterator = accessor.getMostRecentCode(dataSharingAgreementGuid, medication, codeId).iterator();
        if (iterator.hasNext()) {
            CassandraEmisCsvCodeMap result = iterator.next();
            return new EmisCsvCodeMap(result);
        } else {
            return null;
        }
    }

    public void save(EmisAdminResourceCache resourceCache) {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        CassandraEmisAdminResourceCache cassandraObj = new CassandraEmisAdminResourceCache(resourceCache);

        Mapper<CassandraEmisAdminResourceCache> mapper = getMappingManager().mapper(CassandraEmisAdminResourceCache.class);
        mapper.save(cassandraObj);
    }

    public void delete(EmisAdminResourceCache resourceCache) {
        if (resourceCache == null) {
            throw new IllegalArgumentException("resourceCache is null");
        }

        CassandraEmisAdminResourceCache cassandraObj = new CassandraEmisAdminResourceCache(resourceCache);

        Mapper<CassandraEmisAdminResourceCache> mapper = getMappingManager().mapper(CassandraEmisAdminResourceCache.class);
        mapper.delete(cassandraObj);
    }

    public List<EmisAdminResourceCache> getCachedResources(String dataSharingAgreementGuid) {

        EmisAccessor accessor = getMappingManager().createAccessor(EmisAccessor.class);
        List<CassandraEmisAdminResourceCache> results = Lists.newArrayList(accessor.getCachedResources(dataSharingAgreementGuid));

        return results
                .stream()
                .map(T -> new EmisAdminResourceCache(T))
                .collect(Collectors.toList());
    }
}
