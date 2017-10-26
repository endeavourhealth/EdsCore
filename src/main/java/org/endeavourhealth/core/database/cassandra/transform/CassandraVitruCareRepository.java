package org.endeavourhealth.core.database.cassandra.transform;

import com.datastax.driver.mapping.Mapper;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.transform.accessors.VitruCareAccessor;
import org.endeavourhealth.core.database.cassandra.transform.models.CassandraVitruCarePatientIdMap;
import org.endeavourhealth.core.database.dal.subscriberTransform.VitruCareTransformDalI;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class CassandraVitruCareRepository extends Repository implements VitruCareTransformDalI {

    public void saveVitruCareIdMapping(UUID edsPatientId, UUID serviceId, UUID systemId, String virtruCareId) {
        CassandraVitruCarePatientIdMap o = new CassandraVitruCarePatientIdMap();
        o.setEdsPatientId(edsPatientId);
        o.setServiceId(serviceId);
        o.setSystemId(systemId);
        o.setVitruCareId(virtruCareId);
        o.setCreatedAt(new Date());
        saveVitruCareIdMapping(o);
    }

    private void saveVitruCareIdMapping(CassandraVitruCarePatientIdMap mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null");
        }

        Mapper<CassandraVitruCarePatientIdMap> mapper = getMappingManager().mapper(CassandraVitruCarePatientIdMap.class);
        mapper.save(mapping);
    }

    public String getVitruCareId(UUID edsPatientId) {

        VitruCareAccessor accessor = getMappingManager().createAccessor(VitruCareAccessor.class);
        Iterator<CassandraVitruCarePatientIdMap> iterator = accessor.getVitruCareIdMapping(edsPatientId).iterator();
        if (iterator.hasNext()) {
            CassandraVitruCarePatientIdMap mapping = iterator.next();
            return mapping.getVitruCareId();
        } else {
            return null;
        }
    }
}
